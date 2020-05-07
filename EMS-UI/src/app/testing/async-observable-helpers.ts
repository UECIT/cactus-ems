import { defer } from "rxjs";

/**
 * Create an async observable that emits once and completes
 * @param data to return
 */
export function asyncData<T>(data: T) {
    return defer(() => Promise.resolve(data))
}