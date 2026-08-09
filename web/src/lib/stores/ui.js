// Tiny UI store: toasts + a promise-based confirm to replace window.confirm.
import { writable } from 'svelte/store';

export const toasts = writable([]); // [{ id, kind: 'ok'|'error', message }]

let nextId = 1;
export function toast(message, kind = 'ok', ms = 3500) {
    const id = nextId++;
    toasts.update((t) => [...t, { id, kind, message }]);
    if (ms) setTimeout(() => dismissToast(id), ms);
}
export function dismissToast(id) {
    toasts.update((t) => t.filter((x) => x.id !== id));
}

// Promise-based confirm. Resolves true/false. A single dialog is driven by the store.
export const confirmState = writable(null); // { message, resolve } | null
export function confirmDialog(message) {
    return new Promise((resolve) => {
        confirmState.set({ message, resolve });
    });
}