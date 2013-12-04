/*
 * Copyright 2013 University of Washington
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package piecework.security.concrete;

import org.apache.commons.lang.NotImplementedException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import piecework.model.Secret;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * @author James Renfro
 */
public class PassthroughEncryptionService extends BaseEncryptionService {

    @Override
    public Secret encrypt(String text) throws InvalidCipherTextException, UnsupportedEncodingException, GeneralSecurityException {
        return new Secret.Builder().ciphertext(text.getBytes("UTF-8")).build();
    }

    @Override
    public String decrypt(Secret secret) throws InvalidCipherTextException, GeneralSecurityException, UnsupportedEncodingException {
        return new String(secret.getCiphertext(), "UTF-8");
    }

    @Override
    public String generateKey(int n) throws NoSuchAlgorithmException {
        throw new NotImplementedException();
    }
}
