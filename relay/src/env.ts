export interface Env {
  PUSH_KEYS: KVNamespace;
  SEND_LIMITER: RateLimit;
  REGISTER_LIMITER: RateLimit;
  /** Firebase service account JSON, set with `wrangler secret put FCM_SERVICE_ACCOUNT`. */
  FCM_SERVICE_ACCOUNT: string;
}

/** KV value under `sha256(push_key)`. Times are epoch seconds. */
export interface PushKeyRecord {
  fcm_token: string;
  created: number;
  updated: number;
}
