package dev.taut.websitedemo.Security;


import dev.taut.websitedemo.Accounts.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private static AccountRepository accountRepository;

    public static boolean saveAccount(Account account) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            return false;
        }
        accountRepository.save(account);
        return true;
    }
}
