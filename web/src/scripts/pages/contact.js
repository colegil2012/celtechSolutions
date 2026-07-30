/**
 * scripts/pages/contact.js
 *
 * Contact-form behaviour: validation, payload shaping, submit lifecycle. The
 * component imports these and stays limited to rendering + binding.
 */

import { api, ApiError } from '$lib/api/client.js';

export const BUDGET_RANGES = [
  { value: 'under-1500', label: 'Under $1,500' },
  { value: '1500-5000',  label: '$1,500 – $5,000' },
  { value: '5000-15000', label: '$5,000 – $15,000' },
  { value: 'over-15000', label: 'Over $15,000' },
  { value: 'unsure',     label: 'Not sure yet' }
];

/**
 * `interest` is prefilled when arriving from a package page (the package slug),
 * else "general". Populated dynamically from the packages list plus a general
 * option, so it stays in sync with whatever packages exist.
 */
export function emptyForm(interest = 'general') {
  return {
    name: '', email: '', phone: '', company: '', message: '',
    interest, budgetRange: 'unsure',
    website: '' // honeypot
  };
}

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validate(form) {
  const errors = {};
  if (!form.name.trim()) errors.name = 'Enter your name';
  else if (form.name.trim().length > 120) errors.name = 'Name is too long';

  if (!form.email.trim()) errors.email = 'Enter your email address';
  else if (!EMAIL.test(form.email.trim())) errors.email = 'Enter a valid email address';

  if (!form.message.trim()) errors.message = 'Tell us a little about what you need';
  else if (form.message.trim().length > 4000) errors.message = 'Message is too long';

  return errors;
}

export async function submitForm(form) {
  const errors = validate(form);
  if (Object.keys(errors).length > 0) {
    return { ok: false, message: 'Check the highlighted fields and try again.', fields: errors };
  }
  try {
    const res = await api.submitInquiry({
      name: form.name.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
      company: form.company.trim(),
      message: form.message.trim(),
      interest: form.interest,
      budgetRange: form.budgetRange,
      website: form.website
    });
    return { ok: true, message: res?.message ?? 'Thanks — your message is in.' };
  } catch (error) {
    if (error instanceof ApiError) {
      return { ok: false, message: error.message, fields: error.body?.fields ?? {} };
    }
    return { ok: false, message: 'Something went wrong. Try again in a moment.', fields: {} };
  }
}
