<script>
  import { getContext, onMount } from 'svelte';
  import { portal } from '$lib/api/client.js';
  import { toast, confirmDialog } from '$lib/stores/ui.js';

  const ctx = getContext('portal');
  let site = $derived(ctx.activeSite);

  let inquiries = $state([]);
  let ready = $state(false);
  let filter = $state('all'); // all | NEW | READ | REPLIED | ARCHIVED

  async function refresh() {
    if (!site?.id) return;
    inquiries = (await portal.inquiries(site.id)) ?? [];
  }
  onMount(async () => { await refresh(); ready = true; });
  $effect(() => { if (site?.id) refresh(); });

  let visible = $derived(filter === 'all'
    ? inquiries
    : inquiries.filter((i) => i.status === filter));

  async function setStatus(inq, status) {
    try {
      await portal.updateInquiryStatus(inq.id, status);
      await refresh();
      toast(`Marked ${status.toLowerCase()}.`);
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }
  async function remove(inq) {
    if (!(await confirmDialog(`Delete the lead from ${inq.name}?`))) return;
    try {
      await portal.deleteInquiry(inq.id);
      inquiries = inquiries.filter((i) => i.id !== inq.id);
      toast('Deleted.');
    } catch (e) { toast(e?.message ?? 'Delete failed.', 'error'); }
  }

  function fmt(ts) { return new Date(ts).toLocaleString(); }
</script>

<svelte:head><title>Inbox | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Inbox</h1>
  <p class="pp__lede">Leads submitted through {site?.name ?? 'your'}'s contact form.</p>
</header>

{#if ready}
  <div class="pp__filters">
    {#each ['all', 'NEW', 'READ', 'REPLIED', 'ARCHIVED'] as f}
      <button class="pp__filter" class:pp__filter--on={filter === f}
              onclick={() => (filter = f)}>{f === 'all' ? 'All' : f}</button>
    {/each}
  </div>

  {#if visible.length === 0}
    <p class="pp__empty">No leads in this view.</p>
  {:else}
    <div class="pp__inbox">
      {#each visible as inq (inq.id)}
        <article class="pp__lead pp__lead--{inq.status.toLowerCase()}">
          <div class="pp__lead-head">
            <div>
              <strong>{inq.name}</strong>
              <a class="pp__mono" href={`mailto:${inq.email}`}>{inq.email}</a>
              {#if inq.phone}<span class="pp__mono"> · {inq.phone}</span>{/if}
              {#if inq.company}<span class="pp__panel-sub"> · {inq.company}</span>{/if}
            </div>
            <span class="pp__panel-sub">{fmt(inq.createdAt)}</span>
          </div>
          {#if inq.subject}<div class="pp__panel-sub">Re: {inq.subject}</div>{/if}
          <p class="pp__lead-msg">{inq.message}</p>
          <div class="pp__lead-actions">
            <span class="pp__lead-status">{inq.status}</span>
            <button class="btn" onclick={() => setStatus(inq, 'READ')}>Read</button>
            <button class="btn" onclick={() => setStatus(inq, 'REPLIED')}>Replied</button>
            <button class="btn" onclick={() => setStatus(inq, 'ARCHIVED')}>Archive</button>
            <a class="btn" href={`mailto:${inq.email}`}>Reply</a>
            <button class="btn" onclick={() => remove(inq)}>Delete</button>
          </div>
        </article>
      {/each}
    </div>
  {/if}
{/if}