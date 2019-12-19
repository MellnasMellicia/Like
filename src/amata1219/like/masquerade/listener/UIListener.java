package amata1219.like.masquerade.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.event.ClickEvent;
import amata1219.like.masquerade.event.CloseEvent;
import amata1219.like.masquerade.event.OpenEvent;
import amata1219.like.masquerade.reflection.SafeCast;
import amata1219.like.monad.Maybe;

public class UIListener implements Listener {

	@EventHandler
	public void onOpen(InventoryOpenEvent event){
		extractLayout(event.getInventory()).apply(l -> l.fire(new OpenEvent(event)));
	}

	@EventHandler
	public void onClick(InventoryClickEvent event){
		extractLayout(event.getInventory()).apply(l -> {
			event.setCancelled(true);
			l.fire(new ClickEvent(event));
		});
	}

	@EventHandler
	public void onOpen(InventoryCloseEvent event){
		extractLayout(event.getInventory()).apply(l -> l.fire(new CloseEvent(event)));
	}

	private Maybe<Layout> extractLayout(Inventory inventory){
		return Maybe.unit(inventory)
				.map(Inventory::getHolder)
				.flatMap(x -> SafeCast.cast(x, Layout.class));
	}

}
