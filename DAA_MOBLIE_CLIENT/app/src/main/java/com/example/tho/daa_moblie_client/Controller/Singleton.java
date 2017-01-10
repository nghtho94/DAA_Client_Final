package com.example.tho.daa_moblie_client.Controller;


import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;

public class Singleton {

    private static final String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";
    private static Singleton singleton = new Singleton();
    private static IdentityData AnonymousIdentity;
    private static BNCurve curve;
    private static String sesssionID;

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private Singleton() { }

    /* Static 'instance' method */
    public static Singleton getInstance( ) {
        return singleton;
    }

    protected static void initData(){
        curve = new BNCurve(BNCurve.BNCurveInstantiation.valueOf(TPM_ECC_BN_P256));
    }

    public static IdentityData getAnonymousIdentity() {
        return AnonymousIdentity;
    }

    public static BNCurve getCurve() {
        return curve;
    }

    public static String getSesssionID() {
        return sesssionID;
    }

    public static void setSesssionID(String sesssionID) {
        Singleton.sesssionID = sesssionID;
    }

    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }
}
