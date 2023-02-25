package com.kensbunker.mn.broker.data;

import com.kensbunker.mn.broker.Symbol;
import com.kensbunker.mn.broker.wallet.DepositFiatMoney;
import com.kensbunker.mn.broker.wallet.Wallet;
import com.kensbunker.mn.broker.wallet.WithdrawFiatMoney;
import com.kensbunker.mn.broker.watchlist.WatchList;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.*;

@Singleton
public class InMemoryAccountStore {
  public static final UUID ACCOUNT_ID = UUID.fromString("f4245629-83df-4ed8-90d9-7401045b5921");

  private final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
  private final HashMap<UUID, Map<UUID, Wallet>> walletsPerAccount = new HashMap<>();

  public WatchList getWatchList(final UUID accountId) {
    return watchListPerAccount.getOrDefault(accountId, new WatchList());
  }

  public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
    watchListPerAccount.put(accountId, watchList);
    return getWatchList(accountId);
  }

  public void deleteWatchList(final UUID accountId) {
    watchListPerAccount.remove(accountId);
  }

  public Collection<Wallet> getWallets(UUID accountId) {
    return Optional.ofNullable(walletsPerAccount.get(accountId)).orElse(new HashMap<>()).values();
  }

  public Wallet depositToWallet(DepositFiatMoney deposit) {
    return addAvailableInWallet(
        deposit.accountId(), deposit.walletId(), deposit.symbol(), deposit.amount());
  }

  public Wallet withdrawFromWallet(WithdrawFiatMoney withdraw) {
    return addAvailableInWallet(
        withdraw.accountId(), withdraw.walletId(), withdraw.symbol(), withdraw.amount());
  }

  private Wallet addAvailableInWallet(
      UUID acccountId, UUID walletId, Symbol symbol, BigDecimal changeAmount) {
    final var wallets =
        Optional.ofNullable(walletsPerAccount.get(acccountId)).orElse(new HashMap<>());
    var oldWallet =
        Optional.ofNullable(wallets.get(walletId))
            .orElse(new Wallet(ACCOUNT_ID, walletId, symbol, BigDecimal.ZERO, BigDecimal.ZERO));
    var newWallet = oldWallet.addAvailable(changeAmount);
    // Update wallet in store
    wallets.put(newWallet.walletId(), newWallet);
    walletsPerAccount.put(newWallet.accountId(), wallets);
    return newWallet;
  }
}
