package net.shade.plugin;

import net.shade.plugin.ChatPlugin;
import net.minecraft.util.Identifier;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shade.plugin.SettingPlugin;


import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

import java.util.UUID;

import java.awt.*;

public class CommandGuiPlugin extends Screen{
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    TextFieldWidget c;
    private static String lc;


    public CommandGuiPlugin() {
        super(Text.of("Command"));
    }

    @Override
    protected void init(){
        c = new TextFieldWidget(MC.textRenderer, 58, 130, 522, 20, Text.of("Command"));
        c.setMaxLength(65535);
        if(lc != null){
            c.setText(lc);
        }

        ButtonWidget execute = new ButtonWidget(270, 159, 100, 20, Text.of("Execute"), button ->{
            if(c.getText().startsWith(SettingPlugin.prefix)){
                MC.player.sendChatMessage(c.getText());
            }else{
            }
        });

        this.addDrawable(execute);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(0, 0, 0, 100).getRGB());
        c.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        c.charTyped(chr, keyCode);
        lc = c.getText();
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        c.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        c.keyPressed(keyCode, scanCode, modifiers); 
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        c.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
