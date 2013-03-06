package org.wargamer2010.signshop.operations;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.Block;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.util.itemUtil;
import java.util.List;
import java.util.LinkedList;

public class takeShopItems implements SignShopOperation {
    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        if(ssArgs.getContainables().isEmpty()) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("chest_missing", ssArgs.getMessageParts()));
            return false;
        }
        ItemStack[] isTotalItems = itemUtil.getAllItemStacksForContainables(ssArgs.getContainables().get());

        if(!ssArgs.isOperationParameter("allowemptychest") && isTotalItems.length == 0) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("chest_empty", ssArgs.getMessageParts()));
            return false;
        }
        ssArgs.getItems().set(isTotalItems);
        ssArgs.setMessagePart("!items", itemUtil.itemStackToString(ssArgs.getItems().get()));
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        if(ssArgs.getItems().get() == null) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("no_items_defined_for_shop", ssArgs.getMessageParts()));
            return false;
        }
        
        Boolean bStockOK = itemUtil.stockOKForContainables(ssArgs.getContainables().get(), ssArgs.getItems().get(), true);
        ssArgs.setMessagePart("!items", itemUtil.itemStackToString(ssArgs.getItems().get()));
        if(!bStockOK)
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("out_of_stock", ssArgs.getMessageParts()));
        if(!bStockOK && activeCheck)
            itemUtil.updateStockStatus(ssArgs.getSign().get(), ChatColor.DARK_RED);
        else if(activeCheck)
            itemUtil.updateStockStatus(ssArgs.getSign().get(), ChatColor.DARK_BLUE);

        return bStockOK;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        InventoryHolder Holder = itemUtil.getFirstStockOKForContainables(ssArgs.getContainables().get(), ssArgs.getItems().get(), true);
        if(Holder == null)
            return false;
        Holder.getInventory().removeItem(ssArgs.getItems().get());
        if(!itemUtil.stockOKForContainables(ssArgs.getContainables().get(), ssArgs.getItems().get(), true))
            itemUtil.updateStockStatus(ssArgs.getSign().get(), ChatColor.DARK_RED);
        else
            itemUtil.updateStockStatus(ssArgs.getSign().get(), ChatColor.DARK_BLUE);
        ssArgs.setMessagePart("!items", itemUtil.itemStackToString(ssArgs.getItems().get()));
        return true;
    }
}
