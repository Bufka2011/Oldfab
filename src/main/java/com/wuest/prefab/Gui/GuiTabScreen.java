package com.wuest.prefab.Gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.wuest.prefab.Gui.Controls.GuiTab;
import com.wuest.prefab.Gui.Controls.GuiTabTray;

/**
 * 
 * @author WuestMan
 *
 */
public class GuiTabScreen extends GuiScreen {

    protected GuiTabTray Tabs;

    public GuiTabScreen() {
        this.Tabs = new GuiTabTray();
    }

    /**
     * Processes when this tab is clicked.
     * 
     * @param tab The tab which was clicked.
     */
    protected void tabClicked(GuiTab tab) {}

    protected GuiTab getSelectedTab() {
        return this.Tabs.GetSelectedTab();
    }

    @Override
    public void initGui() {
        this.Tabs.GetTabs()
            .clear();
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw the default labels and buttons.
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw the tabs.
        this.Tabs.DrawTabs(this.mc, mouseX, mouseY);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            // This handles the button presses.
            super.mouseClicked(mouseX, mouseY, mouseButton);

            // Handle the tab clicking.
            ArrayList<GuiTab> guiTabs = this.Tabs.GetTabs();

            for (GuiTab tab : guiTabs) {
                if (tab.mousePressed(mc, mouseX, mouseY)) {
                    tab.playPressSound(mc.getSoundHandler());
                    this.tabClicked(tab);
                    break;
                }
            }
        }
    }

    /**
     * Draws a textured rectangle Args: x, y, z, width, height, textureWidth, textureHeight
     * 
     * @param x             The X-Axis screen coordinate.
     * @param y             The Y-Axis screen coordinate.
     * @param z             The Z-Axis screen coordinate.
     * @param width         The width of the rectangle.
     * @param height        The height of the rectangle.
     * @param textureWidth  The width of the texture.
     * @param textureHeight The height of the texture.
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, int z, int width, int height,
        float textureWidth, float textureHeight) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        float u = 0;
        float v = 0;
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(x, y + height, z, u * f, (v + height) * f1);

        tessellator.addVertexWithUV(x + width, y + height, z, (u + width) * f, (v + height) * f1);

        tessellator.addVertexWithUV(x + width, y, z, (u + width) * f, v * f1);

        tessellator.addVertexWithUV(x, y, z, u * f, v * f1);

        tessellator.draw();
    }
}
