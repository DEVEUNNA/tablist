

package ru.eunna.tablist.events;

import java.awt.image.*;
import net.minecraftforge.client.event.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;
import net.minecraft.client.*;
import org.lwjgl.input.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import ru.eunna.tablist.core.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.entity.*;
import net.minecraft.util.*;
import net.minecraft.client.network.*;
import net.minecraft.scoreboard.*;
import net.minecraft.client.renderer.*;
import java.util.*;

public class GuiEvents
{
    public static Map<String, Boolean> checker;
    public static Map<String, Boolean> done;
    public static Map<String, BufferedImage> icons;
    public static Map<String, ResourceLocation> res;
    public static List<String> names;
    private boolean toggle;
    private int biggestPlayerWidth;
    
    public GuiEvents() {
        this.toggle = false;
        this.biggestPlayerWidth = 0;
    }
    
    @SubscribeEvent
    public void onTabListDrawed(final RenderGameOverlayEvent.Pre e) {
        if ((e.type == RenderGameOverlayEvent.ElementType.HOTBAR || e.type == RenderGameOverlayEvent.ElementType.HEALTH || e.type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.type == RenderGameOverlayEvent.ElementType.FOOD || e.type == RenderGameOverlayEvent.ElementType.ARMOR) && this.toggle) {
            e.setCanceled(true);
        }
        if (e.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            e.setCanceled(true);
            this.renderTabList();
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getKeyCode())) {
            this.toggle = true;
        }
        else {
            this.toggle = false;
        }
    }
    
    private int get_admins_count(final List<GuiPlayerInfo> players) {
        int result = 0;
        final String[] codes = Core.admins_prefixes.split(";");
        for (final GuiPlayerInfo player : players) {
            final ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(player.name);
            final String displayname = ScorePlayerTeam.formatPlayerName((Team)team, player.name);
            for (final String code : codes) {
                if (displayname.contains(code.replace("&", "�"))) {
                    ++result;
                    break;
                }
            }
        }
        return result;
    }
    
    private void renderTabList() {
        final Minecraft mc = Minecraft.getMinecraft();
        final List players = Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoList;
        final int players_count = players.size();
        final ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int width = res.getScaledWidth();
        final int height = res.getScaledHeight();
        final ScoreObjective scoreobjective = mc.theWorld.getScoreboard().func_96539_a(0);
        final NetHandlerPlayClient handler = mc.thePlayer.sendQueue;
        if (mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            int rows;
            int maxPlayers;
            int columns;
            for (maxPlayers = (rows = handler.currentServerMaxPlayers), columns = 1, columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
                ++columns;
            }
            int columnWidth = 300 / columns;
            this.biggestPlayerWidth = this.getBiggestWidth(players);
            if (columnWidth < this.biggestPlayerWidth) {
                columnWidth = this.biggestPlayerWidth;
            }
            final int left = (width - columns * columnWidth) / 2;
            Gui.drawRect(left - 1, 1, left + columnWidth * columns, 13, -1879048192);
            mc.fontRenderer.drawStringWithShadow("�6\u0418\u0433\u0440\u043e\u043a\u0438 \u043e\u043d\u043b\u0430\u0439\u043d", width / 2 - mc.fontRenderer.getStringWidth("\u0418\u0433\u0440\u043e\u043a\u0438 \u043e\u043d\u043b\u0430\u0439\u043d") / 2, 3, 16777215);
            final byte border = 15;
            final int admins_count = this.get_admins_count(players);
            boolean draw_personal = false;
            Gui.drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows + 2, Integer.MIN_VALUE);
            if (admins_count > 0) {
                draw_personal = true;
            }
            if (Core.personal_calc && draw_personal) {
                Gui.drawRect(left - 1, border + 9 * rows + 2 + 5, left + columnWidth * columns, border + 9 * rows + 2 + 30, -1879048192);
                mc.fontRenderer.drawStringWithShadow("�e " + players.size() + " �6\u0438\u0433\u0440\u043e\u043a" + this.getNumEnding(players.size(), new String[] { "", "\u0430", "\u043e\u0432" }) + " \u0438\u0437 �e" + maxPlayers, width / 2 - mc.fontRenderer.getStringWidth(players.size() + " �6\u0438\u0433\u0440\u043e\u043a" + this.getNumEnding(players.size(), new String[] { "", "\u0430", "\u043e\u0432" }) + " \u0438\u0437 �e" + maxPlayers) / 2, border + 9 * rows + 2 + 9, 16777215);
                mc.fontRenderer.drawStringWithShadow("�a\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b \u043e\u043d\u043b\u0430\u0439\u043d: �2" + admins_count + " �a\u0447\u0435\u043b.", width / 2 - mc.fontRenderer.getStringWidth("\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b \u043e\u043d\u043b\u0430\u0439\u043d: " + admins_count + " \u0447\u0435\u043b.") / 2, border + 9 * rows + 2 + 18, 16777215);
            }
            else {
                Gui.drawRect(left - 1, border + 9 * rows + 3, left + columnWidth * columns, border + 9 * rows + 17, -1879048192);
                mc.fontRenderer.drawStringWithShadow("�e " + players.size() + " �6\u0438\u0433\u0440\u043e\u043a" + this.getNumEnding(players.size(), new String[] { "", "\u0430", "\u043e\u0432" }) + " \u0438\u0437 �e" + maxPlayers, width / 2 - mc.fontRenderer.getStringWidth(players.size() + " �6\u0438\u0433\u0440\u043e\u043a" + this.getNumEnding(players.size(), new String[] { "", "\u0430", "\u043e\u0432" }) + " \u0438\u0437 �e" + maxPlayers) / 2, border + 9 * rows + 6, 16777215);
            }
            this.draw_copyright(height, width, mc);
            for (int i = 0; i < maxPlayers; ++i) {
                final int xPos = left + i % columns * columnWidth;
                final int yPos = border + i / columns * 9;
                Gui.drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 10, 553648127);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glEnable(3008);
                if (i < players_count) {
                    final GuiPlayerInfo pl = (GuiPlayerInfo) players.get(i);
                    final String realName = pl.name;
                    if (GuiEvents.checker.getOrDefault(this.get_clean_nick(realName, 1), true)) {
                        GuiEvents.checker.put(this.get_clean_nick(realName, 1), false);
                        new Thread((Runnable)new HeadThread(this.get_clean_nick(realName, 1))).start();
                    }
                    if (GuiEvents.done.getOrDefault(this.get_clean_nick(realName, 1), false)) {
                        if (!GuiEvents.res.containsKey(this.get_clean_nick(realName, 1))) {
                            final ResourceLocation icon = new ResourceLocation("skins/heads/" + this.get_clean_nick(realName, 1));
                            DynamicTexture dt = (DynamicTexture)mc.getTextureManager().getTexture(icon);
                            final BufferedImage bufferedimage = GuiEvents.icons.get(this.get_clean_nick(realName, 1));
                            dt = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                            mc.getTextureManager().loadTexture(icon, (ITextureObject)dt);
                            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), dt.getTextureData(), 0, bufferedimage.getWidth());
                            dt.updateDynamicTexture();
                            GuiEvents.res.put(this.get_clean_nick(realName, 1), icon);
                            GuiEvents.icons.remove(this.get_clean_nick(realName, 1));
                        }
                        this.drawHeadTextureRect(GuiEvents.res.get(this.get_clean_nick(realName, 1)), xPos, yPos, xPos + 9, yPos + 9);
                    }
                    else {
                        this.drawHeadTextureRect(AbstractClientPlayer.locationStevePng, xPos, yPos, xPos + 9, yPos + 9);
                    }
                    final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(this.get_clean_nick(realName, 1));
                    String displayName = "";
                    if (Core.use_scoreboards) {
                        displayName = ScorePlayerTeam.formatPlayerName((Team)team, realName);
                    }
                    else {
                        displayName = realName;
                    }
                    mc.fontRenderer.drawStringWithShadow(displayName, xPos + 11, yPos, 16777215);
                    if (scoreobjective != null) {
                        final int endX = xPos + mc.fontRenderer.getStringWidth(displayName) + 5;
                        final int maxX = xPos + columnWidth - 12 - 5;
                        if (maxX - endX > 5) {
                            final Score score = scoreobjective.getScoreboard().func_96529_a(realName, scoreobjective);
                            final String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            mc.fontRenderer.drawStringWithShadow(scoreDisplay, maxX - mc.fontRenderer.getStringWidth(scoreDisplay), yPos, 16777215);
                        }
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
    }
    
    private void draw_copyright(final int height, final int width, final Minecraft mc) {
        Gui.drawRect(0, height, width, height - 13, 553648127);
        final String copy = Core.copyright.replace("%server_name%", Core.server_name);
        mc.fontRenderer.drawStringWithShadow(copy, width / 2 - mc.fontRenderer.getStringWidth(this.get_clean_nick(copy, 2)) / 2, height - 10, 16777215);
    }
    
    private String get_clean_nick(final String name, final int type) {
        final String[] colors = { "�1", "�2", "�3", "�4", "�5", "�6", "�7", "�8", "�9", "�0", "�a", "�b", "�c", "�d", "�e", "�f", "�r", "�l", "�o", "�m", "�n", "�k" };
        String result = name;
        for (final String prefix : Core.admins_prefixes.split(";")) {
            result = result.replace(prefix.replace("&", "�"), "");
        }
        for (final String color : colors) {
            result = result.replace(color, "");
        }
        final String[] nick_data = result.split(" ");
        if (nick_data.length > 0 && type == 1) {
            result = nick_data[nick_data.length - 1];
        }
        return result;
    }
    
    private int getBiggestWidth(final List<GuiPlayerInfo> players) {
        int i = 0;
        for (final GuiPlayerInfo info : players) {
            final ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(info.name);
            final String displayName = ScorePlayerTeam.formatPlayerName((Team)team, info.name);
            final int size = Minecraft.getMinecraft().fontRenderer.getStringWidth(displayName) + 12;
            if (size > i) {
                i = size;
            }
        }
        return i;
    }
    
    private void drawTexturedModalRect(final int p_73729_1_, final int p_73729_2_, final int p_73729_3_, final int p_73729_4_, final int p_73729_5_, final int p_73729_6_) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), 0.0, (double)((p_73729_3_ + 0) * f), (double)((p_73729_4_ + p_73729_6_) * f2));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), 0.0, (double)((p_73729_3_ + p_73729_5_) * f), (double)((p_73729_4_ + p_73729_6_) * f2));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), 0.0, (double)((p_73729_3_ + p_73729_5_) * f), (double)((p_73729_4_ + 0) * f2));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), 0.0, (double)((p_73729_3_ + 0) * f), (double)((p_73729_4_ + 0) * f2));
        tessellator.draw();
    }
    
    private void drawHeadTextureRect(final ResourceLocation res, final int x1, final int y1, final int x2, final int y2) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        final Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV((double)x1, (double)y2, 0.0, 0.125, 0.5);
        t.addVertexWithUV((double)x2, (double)y2, 0.0, 0.25, 0.5);
        t.addVertexWithUV((double)x2, (double)y1, 0.0, 0.25, 0.25);
        t.addVertexWithUV((double)x1, (double)y1, 0.0, 0.125, 0.25);
        t.draw();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    private String getNumEnding(final int num, final String[] ends_arrays) {
        int number = num;
        String result = "";
        number %= 100;
        if (number >= 11 && number <= 19) {
            result = ends_arrays[2];
        }
        else {
            final int i = number % 10;
            switch (i) {
                case 1: {
                    result = ends_arrays[0];
                    break;
                }
                case 2:
                case 3:
                case 4: {
                    result = ends_arrays[1];
                    break;
                }
                default: {
                    result = ends_arrays[2];
                    break;
                }
            }
        }
        return result;
    }
    
    private void drawTextureRect(final ResourceLocation res, final int x1, final int y1, final int x2, final int y2) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glEnable(3042);
        final Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV((double)x1, (double)y2, 0.0, 0.0, 1.0);
        t.addVertexWithUV((double)x2, (double)y2, 0.0, 1.0, 1.0);
        t.addVertexWithUV((double)x2, (double)y1, 0.0, 1.0, 0.0);
        t.addVertexWithUV((double)x1, (double)y1, 0.0, 0.0, 0.0);
        t.draw();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    static {
        GuiEvents.checker = new HashMap<String, Boolean>();
        GuiEvents.done = new HashMap<String, Boolean>();
        GuiEvents.icons = new HashMap<String, BufferedImage>();
        GuiEvents.res = new HashMap<String, ResourceLocation>();
        GuiEvents.names = new ArrayList<String>();
    }
}
