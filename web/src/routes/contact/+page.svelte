<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { api } from '$lib/api/client.js';
  import { BUDGET_RANGES, emptyForm, submitForm } from '$scripts/pages/contact.js';
  import './page.css';

  let form = $state(emptyForm());
  let errors = $state({});
  let state = $state('idle'); // idle | submitting | success | error
  let feedback = $state('');
  let packages = $state([]);

  onMount(async () => {
    // Prefill interest from ?interest=<slug>
    const interest = $page.url.searchParams.get('interest');
    if (interest) form.interest = interest;
    try { packages = (await api.packages()) ?? []; } catch { packages = []; }
  });

  async function onSubmit(e) {
    e.preventDefault();
    state = 'submitting'; errors = {}; feedback = '';
    const result = await submitForm(form);
    if (result.ok) {
      state = 'success'; feedback = result.message; form = emptyForm();
    } else {
      state = 'error'; feedback = result.message; errors = result.fields ?? {};
    }
  }
</script>

<svelte:head><title>Contact | Celtech Solutions</title>
  <meta name="description" content="Tell us what your business needs. We'll tell you the fastest way to build it." /></svelte:head>

<div class="contact shell">
  <div class="contact__intro">
    <span class="eyebrow">Contact</span>
    <h1 class="contact__title">Start a project</h1>
    <p class="contact__lede">Tell us what your business needs. We reply within one business day.</p>
    <dl class="contact__facts">
      <div><dt>Email</dt><dd><a href="mailto:druid@celtechsolutions.tech">druid@celtechsolutions.tech</a></dd></div>
      <div><dt>Based in</dt><dd>Louisville, Kentucky</dd></div>
    </dl>
  </div>

  <div class="contact__formwrap">
    {#if state === 'success'}
      <div class="formok">
        <h2 class="formok__title">Message sent</h2>
        <p>{feedback}</p>
        <button class="btn" onclick={() => (state = 'idle')}>Send another</button>
      </div>
    {:else}
      <form class="form" onsubmit={onSubmit} novalidate>
        {#if state === 'error' && feedback}<p class="form__alert" role="alert">{feedback}</p>{/if}

        <div class="form__row">
          <label class="field">
            <span class="field__label">Name</span>
            <input class="field__input" class:field__input--err={errors.name} bind:value={form.name} autocomplete="name" />
            {#if errors.name}<span class="field__err">{errors.name}</span>{/if}
          </label>
          <label class="field">
            <span class="field__label">Email</span>
            <input class="field__input" class:field__input--err={errors.email} type="email" bind:value={form.email} autocomplete="email" />
            {#if errors.email}<span class="field__err">{errors.email}</span>{/if}
          </label>
        </div>

        <div class="form__row">
          <label class="field">
            <span class="field__label">Phone <span class="field__opt">optional</span></span>
            <input class="field__input" type="tel" bind:value={form.phone} autocomplete="tel" />
          </label>
          <label class="field">
            <span class="field__label">Business <span class="field__opt">optional</span></span>
            <input class="field__input" bind:value={form.company} autocomplete="organization" />
          </label>
        </div>

        <div class="form__row">
          <label class="field">
            <span class="field__label">Interested in</span>
            <select class="field__input" bind:value={form.interest}>
              <option value="general">General inquiry</option>
              {#each packages as p}<option value={p.slug}>{p.name}</option>{/each}
            </select>
          </label>
          <label class="field">
            <span class="field__label">Budget</span>
            <select class="field__input" bind:value={form.budgetRange}>
              {#each BUDGET_RANGES as b}<option value={b.value}>{b.label}</option>{/each}
            </select>
          </label>
        </div>

        <label class="field">
          <span class="field__label">What do you need?</span>
          <textarea class="field__input field__input--area" class:field__input--err={errors.message} rows="5" bind:value={form.message}></textarea>
          {#if errors.message}<span class="field__err">{errors.message}</span>{/if}
        </label>

        <!-- honeypot -->
        <input class="hp" tabindex="-1" autocomplete="off" bind:value={form.website} aria-hidden="true" />

        <button class="btn btn--solid form__submit" type="submit" disabled={state === 'submitting'}>
          {state === 'submitting' ? 'Sending…' : 'Send message'}
        </button>
      </form>
    {/if}
  </div>
</div>
