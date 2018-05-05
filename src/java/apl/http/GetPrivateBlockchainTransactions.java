/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2017 Jelurida IP B.V.
 * Copyright © 2018 Apollo Foundation
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Apollo Foundation B.V.,
 * no part of the Apl software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package apl.http;

import apl.Account;
import apl.Apl;
import apl.AplException;
import apl.Transaction;
import apl.crypto.Crypto;
import apl.db.DbIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetPrivateBlockchainTransactions extends APIServlet.APIRequestHandler {

    static final GetPrivateBlockchainTransactions instance = new GetPrivateBlockchainTransactions();

    private GetPrivateBlockchainTransactions() {
        super(new APITag[] {APITag.ACCOUNTS, APITag.TRANSACTIONS}, "secretPhrase");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws AplException {

        String secretPhrase = ParameterParser.getSecretPhrase(req, true);
        long accountId = Account.getId(Crypto.getPublicKey(secretPhrase));
        JSONArray transactions = new JSONArray();
        try (DbIterator<? extends Transaction> iterator = Apl.getBlockchain().getTransactions(accountId, (byte)0,(byte)1, 0,false,true)) {
            while (iterator.hasNext()) {
                Transaction transaction = iterator.next();
                transactions.add(JSONData.transaction(transaction, false));
            }
        }
        JSONObject response = new JSONObject();
        response.put("transactions", transactions);
        return response;
    }

}