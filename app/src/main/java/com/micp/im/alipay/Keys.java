/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 *
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.micp.im.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    //合作身份者id，以2088开头的16位纯数字
    public static String DEFAULT_PARTNER = "2088531409336994";

    //收款支付宝账号
    public static String DEFAULT_SELLER = "yswtrue@seatower.cn";

    //将RSA私钥转换成PKCS8格式
    public static String PRIVATE = "aliapp_key_android=MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgcWRzkGZaPWZn" +
            "KQNsWpbG41PPXlc9smImNJ2dLymlSiaxT3eOml18EDUUV5RUqfau/q18ERG8Iieo" +
            "fRc0QphvCOmDjr1my1TBHLXUDd4cIkiApFbC9OojB8cuvkmugovqSMt4k1PzErvd" +
            "A8h+jol0Ju/q5vlRH2c279Ve5+/VFz56Wsau161JaWHaJKH/ynK27HO6CwsdEFP9" +
            "GcLizCPFFZcay08ZYzZ7U54vY8Q2ub3UjSWjJnESd7eL80h/mqQxbuKbXt9CO0ed" +
            "sbBvp1u8Vm3/UhZ4tbwCbV6psJNCkaYBF8xBSNaWQy24s1eT5aEmOJ8jOrg0rbKF" +
            "G4qC+o09AgMBAAECggEAeDP8Gki5JiqSKf+kPO2xwNhX1ijMG8Eln5IT88t93AQt" +
            "8PGklQY5u9U4r0DeNIAR/RF5UFZJfzXnmGf/lnu+UJHQwWuTowv7N6MW5wRsv48/" +
            "2oFFL1Lmh9Av/mk6MrBDuwdGydM3Ngl3DiLjd0261XbGkKrzZz+ryJDuvuft2ysb" +
            "IFBcLxE4oa9bVCJDDUZFUr6kdpdlNpB4d9zemLzekD7mdvo08lBHLeFagKrG4ZDP" +
            "Ucx4mK6zeImNwk+vxX5Ow44NGrPycT2TmDsOUkHRKCx8Lnzug83dxnpZSEXkkFT4" +
            "AmSFPGCJzAyApCRM4wJspM+2sHlMWMaB3LT8VgaIAQKBgQDRd4Kbyjb9kUGhluUD" +
            "itandmskb3upOJXsrOfiprrQ0VJCQ+hp5lKKpSSZXYDAENnHYcjkbc5tfFjLPogj" +
            "6ohZL8K8DK2+3MJogwfPwnQ6lLhAwGz7fe3llinitoft04dWII34u7a64xaTILpE" +
            "eSJZDaexxkLFzV2lFpLvFIeygQKBgQDEFd4BJMAt+89sUob4MzWO7tCQepHoK5C+" +
            "p2e2M9/RJHPmYSIbwSWE02g5IrHcxWuVv8QN9e0bwwRiDX08CdnQs2XdTy6GKh4a" +
            "vb0tKtT4b0S54QTc6P0tbgkhzJShkFv1iOwOnwv5zoueErkQZZ3OaKDM/p5pD1nB" +
            "4HOps6HEvQKBgH8+3nTNTsKS7w++nricKvHoGgY0Qyb1XP49u1AQgQRBZHskzZcn" +
            "d5UJ5UXVa1VB50obuPIrUm9oTniJMCLBYrrrLJ0YBqQfb2oVzzUCp6C+n3+WZZ4R" +
            "TedeZWt3/vMWwOwtMpGbVtIJCbmu8/iMrbTzep+gFQgcTXrwT6o1CooBAoGBAKma" +
            "eHHoxcmAWsNJBrE8KgE9k2zEd9ungwVFazFv4RHXQ+Bq98Ol5rvi1+1DkzBwGkRm" +
            "oD+rDbHLlsjw/femVZ5s7Zjk2kr1tyBOKYkd2SQhqgJVHY2ugP6AhKmVeKHeaYwR" +
            "KIzlCMjD3IOXkjcajcpxOVTWj1I0EuDdMW4Bj9TJAoGAdc54Exl1htQopslB+Jab" +
            "G95/EmryP1B40jt4xXEV94Q6vHXqMKkXqsXBqv/9AE+EjE1XJahZS2Btb58E32X7" +
            "3e5auMhVjGIDouVJZkCObPfnJ2eozWoJ1xcIajXvWk4coNMJEFLnS4Ylc0bYr6Yh" +
            "ltea1F0q/KxU6Pf1pMNju7A=";


}
