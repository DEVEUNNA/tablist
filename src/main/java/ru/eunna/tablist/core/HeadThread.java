
package ru.eunna.tablist.core;

import javax.imageio.*;
import ru.eunna.tablist.events.*;
import java.net.*;
import java.io.*;
import java.awt.image.*;

public class HeadThread implements Runnable
{
    private String name;
    
    public HeadThread(final String name) {
        this.name = name;
    }
    
    @Override
    public void run() {
        this.updateResource(Core.url + this.name + ".png");
    }
    
    private synchronized void updateResource(final String urls) {
        try {
            final URL url = new URL(urls);
            if (((HttpURLConnection)url.openConnection()).getResponseCode() == 404) {
                return;
            }
            final BufferedImage bufferedimage = ImageIO.read(url);
            GuiEvents.icons.put(this.name, bufferedimage);
            GuiEvents.done.put(this.name, true);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
