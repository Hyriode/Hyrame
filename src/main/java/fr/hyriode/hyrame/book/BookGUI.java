package fr.hyriode.hyrame.book;

import fr.hyriode.hyrame.utils.SerializerUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 14/07/2022 at 23:13
 */
public class BookGUI {

    protected final Player owner;
    private final List<TextComponent> components;

    public BookGUI(Player owner) {
        this.owner = owner;
        this.components = new ArrayList<>();
    }

    public BookGUI addComponent(TextComponent component) {
        this.components.add(component);
        return this;
    }

    public BookGUI addComponent(String text) {
        this.components.add(new TextComponent(text));
        return this;
    }

    public BookGUI addLine(String line) {
        this.components.add(new TextComponent(line + "\n"));
        return this;
    }

    public BookGUI addLine(TextComponent textComponent) {
        final TextComponent line = new TextComponent(textComponent.getText() + "\n");

        if (textComponent.getHoverEvent() != null) {
            line.setHoverEvent(textComponent.getHoverEvent());
        }

        if (textComponent.getClickEvent() != null) {
            line.setClickEvent(textComponent.getClickEvent());
        }

        this.components.add(line);

        return this;
    }

    @SuppressWarnings("unchecked")
    public void open() {
        try {
            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            final BookMeta bookMeta = (BookMeta) book.getItemMeta();
            final List<IChatBaseComponent> pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(bookMeta);
            final IChatBaseComponent page = IChatBaseComponent.ChatSerializer.a(SerializerUtil.serializeComponent(this.components.toArray(new BaseComponent[0])));
            final Inventory inventory = this.owner.getInventory();
            final ItemStack oldItemStack = inventory.getItem(0);

            pages.add(page);

            bookMeta.setTitle("Book");
            bookMeta.setAuthor("Hyriode");

            book.setItemMeta(bookMeta);

            inventory.setItem(0, book);

            ((CraftPlayer) this.owner).getHandle().openBook(CraftItemStack.asNMSCopy(book));

            inventory.setItem(0, oldItemStack);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
