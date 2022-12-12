package com.skyblock.skyblock.features.bazaar;

import com.skyblock.skyblock.features.bazaar.escrow.Escrow;

import java.util.List;

public interface Bazaar {

    class BazaarItemNotFoundException extends Exception {

        public BazaarItemNotFoundException(String message) {
            super(message);
        }

        public BazaarItemNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    Escrow getEscrow();

    List<BazaarCategory> getCategories();

    BazaarItemData getItemData(BazaarItem item) throws BazaarItemNotFoundException;
    BazaarItemData getSubItemData(BazaarSubItem item) throws BazaarItemNotFoundException;

    default int getCategoryAmount() {
        return this.getCategories().size();
    }

}
