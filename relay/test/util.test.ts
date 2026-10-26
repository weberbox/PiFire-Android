import { describe, expect, it } from "vitest";
import { generatePushKey, sha256Hex } from "../src/util";

describe("util", () => {
  it("hashes with SHA-256", async () => {
    expect(await sha256Hex("abc")).toBe("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
  });

  it("generates unique pfk_ keys of 43 base64url chars", () => {
    const first = generatePushKey();
    expect(first).toMatch(/^pfk_[A-Za-z0-9_-]{43}$/);
    expect(generatePushKey()).not.toBe(first);
  });
});
