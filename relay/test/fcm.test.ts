import { describe, expect, it } from "vitest";
import { signJwt } from "../src/fcm";
import { base64UrlDecode, decodeJwtPart, testServiceAccount } from "./helpers";

describe("signJwt", () => {
  it("signs a Google service account assertion with RS256", async () => {
    const { account, publicKey } = await testServiceAccount();
    const jwt = await signJwt(account, 1761400000);
    const [header, claims, signature] = jwt.split(".") as [string, string, string];

    expect(decodeJwtPart(header)).toEqual({ alg: "RS256", typ: "JWT" });
    expect(decodeJwtPart(claims)).toEqual({
      iss: account.client_email,
      scope: "https://www.googleapis.com/auth/firebase.messaging",
      aud: "https://oauth2.googleapis.com/token",
      iat: 1761400000,
      exp: 1761403600,
    });
    const verified = await crypto.subtle.verify(
      "RSASSA-PKCS1-v1_5",
      publicKey,
      base64UrlDecode(signature),
      new TextEncoder().encode(`${header}.${claims}`),
    );
    expect(verified).toBe(true);
  });
});
