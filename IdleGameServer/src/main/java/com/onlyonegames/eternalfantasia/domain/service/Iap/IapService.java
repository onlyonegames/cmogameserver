package com.onlyonegames.eternalfantasia.domain.service.Iap;
/*
 * right (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.text.TextUtils;
//import android.util.Base64;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Security-related methods. For a secure implementation, all of this code should be implemented on
 * a server that communicates with the application on the device.
 */
@Service
@AllArgsConstructor
public class IapService {
    private static final String TAG = "IABUtil/Security";

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    private static final String Public_Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlau2h7vRtyW9UrHWXkbKp8LEONBPGETFaIWRk0ZkvGlSl3UATrWP+kDLSYeGfg/xOXZ8ch6B31cFJgP0P2g+4MOfkLp5tvOApfSvBsF5HCkr3Vya1fmlBV86gg4XM26Bn+bOyjkVFEMOMdiVBWBDQ02FCr6SBGLC1YmNH73LMTJzix0Wh/VPhw2q7D9l+tbP74rlva6cQ/GUhgzi/2ss9/Ikdru6SIrYNZgTBIlPobfIvMZkYsEV7jA0Vk1x+ZmcotpAgJqPZ1ANp+wC7u3vWQsvi7NeLh4mzMbYZO+yLOgkgNIrvQt82P15icB7LRsSKj+O/hHEs/csa8RYFSmMIwIDAQAB";

    /**
     * Verifies that the data was signed with the given signature, and returns the verified
     * purchase.
//     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    public static boolean verifyPurchase(String signedData,
                                         String signature) throws IOException {
        if (signedData.isEmpty() || signature.isEmpty()) {
            //Purchase verification failed: missing data
            return false;
        }

        PublicKey key = generatePublicKey(Public_Key);
        return verify(key, signedData, signature);
    }
    //구글 결제 프로세스
    //id:long , useridUser, 상품아이디, signedData, signature, 지급되었는지
    /**
     * Generates a PublicKey instance from a string containing the Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    public static PublicKey generatePublicKey(String encodedPublicKey) throws IOException {
        try {

            byte[] decodedKey = Base64.getDecoder().decode(encodedPublicKey);//Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            // "RSA" is guaranteed to be available.
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            String msg = "Invalid key specification: " + e;
            throw new IOException(msg);
        }
    }

    /**
     * Verifies that the signature from the server matches the computed signature on the data.
     * Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        byte[] signatureBytes;
        try {
            signatureBytes = Base64.getDecoder().decode(signature);//Base64.decode(signature, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            //Base64 decoding failed
            return false;
        }
        try {
            Signature signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM);
            signatureAlgorithm.initVerify(publicKey);
            signatureAlgorithm.update(signedData.getBytes());
            if (!signatureAlgorithm.verify(signatureBytes)) {
                //Signature verification failed
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            // "RSA" is guaranteed to be available
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            //Invalid key specification
        } catch (SignatureException e) {
            //Signature exception
        }
        return false;
    }
}
////