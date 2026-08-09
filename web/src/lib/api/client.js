/**
 * Thin wrapper over fetch for the celtech-solutions Spring API.
 *
 * Uses $env/dynamic/public so PUBLIC_API_BASE resolves at RUN time — changing
 * the API origin is an env edit + restart, not a rebuild. In dev it's empty
 * and Vite proxies /api to :8080.
 */

import { env } from '$env/dynamic/public';

const BASE = env.PUBLIC_API_BASE ?? '';

export class ApiError extends Error {
  constructor(message, status, body) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

async function request(path, options = {}) {
  let response;
  const { isForm, ...rest } = options;
  try {
    response = await fetch(`${BASE}${path}`, {
      headers: isForm
          ? { ...options.headers }
          : { 'Content-Type': 'application/json', ...options.headers },
      credentials: 'include',
      ...rest
    });
  } catch {
    throw new ApiError('Could not reach the server. Check your connection and try again.', 0, null);
  }

  const isJson = response.headers.get('content-type')?.includes('application/json');
  const body = isJson ? await response.json().catch(() => null) : null;

  if (!response.ok) {
    throw new ApiError(body?.message ?? 'Something went wrong. Try again in a moment.', response.status, body);
  }
  return body;
}

export const api = {
  /** @param {{kind?: string, random?: boolean, featured?: boolean, category?: string, limit?: number}} opts */
  entries(opts = {}) {
    const p = new URLSearchParams();
    if (opts.kind) p.set('kind', opts.kind);
    if (opts.random) p.set('random', 'true');
    if (opts.featured) p.set('featured', 'true');
    if (opts.category) p.set('category', opts.category);
    if (opts.limit) p.set('limit', String(opts.limit));
    const qs = p.toString();
    return request(`/api/entries${qs ? `?${qs}` : ''}`);
  },

  packages(opts = {}) {
    const qs = opts.featured ? '?featured=true' : '';
    return request(`/api/packages${qs}`);
  },

  package(slug) {
    return request(`/api/packages/${encodeURIComponent(slug)}`);
  },

  submitInquiry(payload) {
    return request('/api/contact', { method: 'POST', body: JSON.stringify(payload) });
  }
};

export const portal = {
  login(email, password) {
    return request('/api/portal/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  },
  logout() {
    return request('/api/portal/auth/logout', { method: 'POST' });
  },
  me() {
    return request('/api/portal/me');
  },
  changePassword(currentPassword, newPassword) {
    return request('/api/portal/me/password', {
      method: 'POST',
      body: JSON.stringify({ currentPassword, newPassword })
    });
  },

  /** Public read — returns { tags: [...], images: [...] }.
   *  `fresh` appends a cache-buster so the PORTAL always sees live data
   *  (the 30-min Cache-Control is meant for public client sites, not authoring). */
  gallery(storageSlug, fresh = false) {
    const bust = fresh ? `?t=${Date.now()}` : '';
    return request(`/api/sites/${encodeURIComponent(storageSlug)}/gallery${bust}`);
  },

  upload(siteId, file) {
    const fd = new FormData();
    fd.append('file', file);
    // isForm flag tells request() to skip the JSON content-type so the
    // browser sets the multipart boundary itself.
    return request(`/api/portal/sites/${encodeURIComponent(siteId)}/gallery`, {
      method: 'POST', body: fd, isForm: true
    });
  },
  updateImage(imageId, patch) {
    // patch may include { caption, altText, position, tagIds }
    return request(`/api/portal/sites/gallery/${encodeURIComponent(imageId)}`, {
      method: 'PUT', body: JSON.stringify(patch)
    });
  },
  deleteImage(imageId) {
    return request(`/api/portal/sites/gallery/${encodeURIComponent(imageId)}`, {
      method: 'DELETE'
    });
  },

  // ---- Tags (client-managed vocabulary) ----
  tags(siteId) {
    return request(`/api/portal/sites/${encodeURIComponent(siteId)}/tags`);
  },
  createTag(siteId, tag) {
    // tag: { label, slug?, kind?, coverImageId?, position? }
    return request(`/api/portal/sites/${encodeURIComponent(siteId)}/tags`, {
      method: 'POST', body: JSON.stringify(tag)
    });
  },
  updateTag(tagId, patch) {
    return request(`/api/portal/sites/tags/${encodeURIComponent(tagId)}`, {
      method: 'PUT', body: JSON.stringify(patch)
    });
  },
  deleteTag(tagId) {
    return request(`/api/portal/sites/tags/${encodeURIComponent(tagId)}`, {
      method: 'DELETE'
    });
  },

  // ---- Inquiries (client leads) ----
  inquiries(siteId) {
    return request(`/api/portal/sites/${encodeURIComponent(siteId)}/inquiries`);
  },
  updateInquiryStatus(inquiryId, status) {
    return request(`/api/portal/sites/inquiries/${encodeURIComponent(inquiryId)}/status`, {
      method: 'PUT', body: JSON.stringify({ status })
    });
  },
  deleteInquiry(inquiryId) {
    return request(`/api/portal/sites/inquiries/${encodeURIComponent(inquiryId)}`, {
      method: 'DELETE'
    });
  },

  // ---- Admin (ADMIN role only; server-gated) ----
  admin: {
    sites() {
      return request('/api/portal/admin/sites');
    },
    createSite(name, storageSlug, notifyEmail) {
      return request('/api/portal/admin/sites', {
        method: 'POST',
        body: JSON.stringify({ name, storageSlug, notifyEmail })
      });
    },
    updateSite(siteId, patch) {
      return request(`/api/portal/admin/sites/${encodeURIComponent(siteId)}`, {
        method: 'PUT', body: JSON.stringify(patch)
      });
    },
    users() {
      return request('/api/portal/admin/users');
    },
    createUser(user) {
      // user: { email, displayName, password, role, siteIds }
      return request('/api/portal/admin/users', {
        method: 'POST', body: JSON.stringify(user)
      });
    },
    updateUser(userId, patch) {
      return request(`/api/portal/admin/users/${encodeURIComponent(userId)}`, {
        method: 'PUT', body: JSON.stringify(patch)
      });
    },
    resetUserPassword(userId, newPassword) {
      return request(`/api/portal/admin/users/${encodeURIComponent(userId)}/password`, {
        method: 'POST', body: JSON.stringify({ newPassword })
      });
    }
  }
};