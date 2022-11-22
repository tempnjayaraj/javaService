package com.example.springboot;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;

public class ROPCClass {

    private static String authority;
    private static Set<String> scope;
    private static String clientId;
    private static String username;
    private static String password;

    private static IAuthenticationResult acquireTokenUsernamePassword(PublicClientApplication pca,
                                                                      Set<String> scope,
                                                                      IAccount account,
                                                                      String username,
                                                                      String password) throws Exception {
        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scope)
                            .account(account)
                            .build();
            result = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, username, password.toCharArray())
                                .build();
                result = pca.acquireToken(parameters).join();
            } else {
                result = null;
            }
        }
        return result;
    }

    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (accounts.isEmpty()) {
        } else {
            for (IAccount account : accounts) {
                if (account.username().equals(username)) {
                    return account;
                }
            }
        }
        return null;
    }

     public static boolean work(String username, String password, String clientID, String tenantID) throws IOException{
        setUpSampleData(username,password,clientID,tenantID);

        PublicClientApplication pca = PublicClientApplication.builder(clientId)
                .authority(authority)
                .build();

               Set<IAccount> accountsInCache = pca.getAccounts().join();
        IAccount account = getAccountByUsername(accountsInCache, username);
        IAuthenticationResult result = null;
        try{
         result = acquireTokenUsernamePassword(pca, scope, account, username, password);
        } catch(Exception e){

        }

        if(result!=null){
            System.out.println("User is Authenticated");
            return true;
        }else{
            System.out.println("Not able to authenticate User");
            return false;
        }
     }

    private static void setUpSampleData(String uname, String pword, String _clientID, String tenantID) throws IOException {

        authority = "https://login.microsoftonline.com/"+tenantID+"/";
        scope = Collections.singleton("user.read");
        clientId = _clientID;
        username = uname;
        password = pword;
    }
}
