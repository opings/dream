package zyz.free.service.test;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class UrlCounter {
    private final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    public AtomicInteger get(String url) {
        AtomicInteger atomicInteger = counterMap.get(url);
        if (Objects.nonNull(atomicInteger)) {
            return atomicInteger;
        }
        counterMap.putIfAbsent(url, new AtomicInteger(0));
        return counterMap.get(url);
    }


    public void incr(String url) {
        AtomicInteger atomicInteger = this.get(url);
        atomicInteger.getAndAdd(1);
    }


    public static void main(String[] args) {
        for (int i = 0; i < 56; i++) {
            String s = "=B" + i + "*(1+C2)";
            System.out.println(s);
        }
    }
}
//
//class Account {
//    int id;
//    int volatile balance;
//}
//
//class BankService {
//    public void transfer(Account from, Account to, Integer amount) {
//        Account a1,a2;
//        if (from.id < to.id) {
//            a1 = from;
//            a2 = to;
//        } else{
//            a1 = to;
//            a2 = from;
//        }
//
//
//        synchronized (a1) {
//            synchronized (a2) {
//                if (from.balance < amount) {
//                    throw new BalanceInsufficientException("you are such a poor guy");
//                }
//                from.balance -= amount;
//                to.blance += amount;
//            }
//        }
//    }
//}