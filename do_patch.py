import sys, os

os.chdir(os.path.dirname(os.path.abspath(__file__)))

with open("index.html", encoding="utf-8") as f:
    content = f.read()

errors = []

# ===== 1. Replace dev-note with statsTable block =====
dev_marker = "rgba(200,168,75,.06)"
if dev_marker in content:
    idx = content.find(dev_marker)
    start = content.rfind("\n    <div", 0, idx)
    end = content.find("</div>", idx) + 6
    new_block = (
        "\n    <div style=\"margin-top:24px\">\n"
        "      <div class=\"section-label\" style=\"margin-bottom:12px\">\u0422\u0430\u0431\u043b\u0438\u0446\u0430 \u043b\u0438\u0434\u0435\u0440\u043e\u0432</div>\n"
        "      <div style=\"display:flex;gap:8px;margin-bottom:16px\">\n"
        "        <button id=\"tab-all\" onclick=\"switchStatsTab('all')\" "
        "style=\"padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);"
        "background:var(--card,#1a1a1a);color:var(--text,#fff);cursor:pointer;font-size:16px;font-weight:600\">"
        "\u0412\u0441\u0435 \u0438\u0433\u0440\u043e\u043a\u0438</button>\n"
        "        <button id=\"tab-online\" onclick=\"switchStatsTab('online')\" "
        "style=\"padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);"
        "background:transparent;color:var(--muted,#888);cursor:pointer;font-size:16px;font-weight:600\">"
        "\u041e\u043d\u043b\u0430\u0439\u043d</button>\n"
        "      </div>\n"
        "      <div id=\"statsTable\"></div>\n"
        "    </div>"
    )
    content = content[:start] + new_block + content[end:]
    print("OK: statsTable block inserted")
else:
    errors.append("statsTable: dev note marker not found")

# ===== 2. Replace createCommModal + add editCommModal =====
cm_marker = "<!-- MODAL: Create community -->"
cm_start = content.find(cm_marker)
if cm_start == -1:
    errors.append("createCommModal not found")
else:
    search_from = cm_start + len(cm_marker)
    modal_end = content.find("\n</div>", search_from)
    modal_end = content.find("\n</div>", modal_end + 1)
    cm_end = modal_end + len("\n</div>")

    new_modals = (
        "<!-- MODAL: Create community -->\n"
        "<div class=\"overlay\" id=\"createCommModal\" onclick=\"closeOnOverlay(event)\">\n"
        "  <div class=\"modal\">\n"
        "    <button class=\"modal-close\" onclick=\"closeModal('createCommModal')\">\u00d7</button>\n"
        "    <h2>\u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043e\u0431\u0449\u0438\u043d\u0443</h2>\n"
        "    <div class=\"inp-group\">\n"
        "      <label>\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043e\u0431\u0449\u0438\u043d\u044b</label>\n"
        "      <input type=\"text\" id=\"commName\" placeholder=\"\u0421\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0444\u043e\u0440\u0442\" maxlength=\"40\"/>\n"
        "    </div>\n"
        "    <div class=\"modal-actions\">\n"
        "      <button class=\"btn btn-ghost\" onclick=\"closeModal('createCommModal')\">\u041e\u0442\u043c\u0435\u043d\u0430</button>\n"
        "      <button class=\"btn btn-primary\" onclick=\"createCommunity()\">\u0421\u043e\u0437\u0434\u0430\u0442\u044c</button>\n"
        "    </div>\n"
        "  </div>\n"
        "</div>\n"
        "\n"
        "<!-- MODAL: Edit community -->\n"
        "<div class=\"overlay\" id=\"editCommModal\" onclick=\"closeOnOverlay(event)\">\n"
        "  <div class=\"modal modal-wide\">\n"
        "    <button class=\"modal-close\" onclick=\"closeModal('editCommModal')\">\u00d7</button>\n"
        "    <h2>\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043e\u0431\u0449\u0438\u043d\u044b</h2>\n"
        "    <div class=\"inp-group\"><label>\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435</label>"
        "<input type=\"text\" id=\"editCommName\" maxlength=\"40\"/></div>\n"
        "    <div class=\"inp-group\"><label>\u0418\u043a\u043e\u043d\u043a\u0430 (\u044d\u043c\u043e\u0434\u0437\u0438)</label>"
        "<input type=\"text\" id=\"editCommIcon\" maxlength=\"2\"/></div>\n"
        "    <div class=\"inp-group\"><label>\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435</label>"
        "<textarea id=\"editCommDesc\" rows=\"3\"></textarea></div>\n"
        "    <div class=\"inp-group\"><label>\u0422\u0435\u043c\u0430\u0442\u0438\u043a\u0430</label>"
        "<input type=\"text\" id=\"editCommTag\" placeholder=\"\u0421\u0442\u0440\u043e\u0438\u0442\u0435\u043b\u044c\u0441\u0442\u0432\u043e, \u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f, PvP...\"/></div>\n"
        "    <div class=\"inp-group\"><label>\u0411\u0430\u043d\u043d\u0435\u0440 (\u0441\u0441\u044b\u043b\u043a\u0430 \u0441 planetminecraft.com/banner)</label>"
        "<input type=\"text\" id=\"editCommBanner\" placeholder=\"https://...\"/></div>\n"
        "    <div class=\"inp-group\"><label>Discord \u0441\u0435\u0440\u0432\u0435\u0440</label>"
        "<input type=\"text\" id=\"editCommDiscord\" placeholder=\"https://discord.gg/...\"/></div>\n"
        "    <div class=\"modal-actions\">\n"
        "      <button class=\"btn btn-ghost\" onclick=\"closeModal('editCommModal')\">\u041e\u0442\u043c\u0435\u043d\u0430</button>\n"
        "      <button class=\"btn btn-primary\" onclick=\"saveCommEdit()\">\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c</button>\n"
        "    </div>\n"
        "  </div>\n"
        "</div>"
    )
    content = content[:cm_start] + new_modals + content[cm_end:]
    print("OK: createCommModal replaced + editCommModal added")

# ===== 3. Replace old JS block =====
js_start_marker = "function openCreateComm()"
js_end_marker = "document.getElementById('stat-online').textContent = '0';"
js_start = content.find(js_start_marker)
js_end_idx = content.find(js_end_marker)

if js_start == -1 or js_end_idx == -1:
    errors.append(f"JS markers not found: start={js_start} end={js_end_idx}")
else:
    js_end = js_end_idx + len(js_end_marker)
    line_start = content.rfind("\n", 0, js_start) + 1

    # Build new JS using list concatenation to avoid quote issues
    L = []
    L.append("  // ---- STATS ----\n")
    L.append("  let lastLeaders = [];\n")
    L.append("  let currentStatsTab = 'all';\n\n")
    L.append("  function switchStatsTab(tab) {\n")
    L.append("    currentStatsTab = tab;\n")
    L.append("    document.getElementById('tab-all').style.background = tab === 'all' ? 'var(--card,#1a1a1a)' : 'transparent';\n")
    L.append("    document.getElementById('tab-all').style.color = tab === 'all' ? 'var(--text,#fff)' : 'var(--muted,#888)';\n")
    L.append("    document.getElementById('tab-online').style.background = tab === 'online' ? 'var(--card,#1a1a1a)' : 'transparent';\n")
    L.append("    document.getElementById('tab-online').style.color = tab === 'online' ? 'var(--text,#fff)' : 'var(--muted,#888)';\n")
    L.append("    renderStatsTable(lastLeaders);\n")
    L.append("  }\n\n")
    L.append("  function renderStatsTable(leaders) {\n")
    L.append("    const tbl = document.getElementById('statsTable');\n")
    L.append("    if (!tbl) return;\n")
    L.append("    if (!leaders || leaders.length === 0) {\n")
    L.append("      tbl.innerHTML = '<div style=\"color:var(--muted)\">\u041d\u0435\u0442 \u0434\u0430\u043d\u043d\u044b\u0445</div>';\n")
    L.append("      return;\n")
    L.append("    }\n")
    L.append("    const filtered = currentStatsTab === 'online' ? leaders.filter(p => p.is_online) : leaders;\n")
    L.append("    if (filtered.length === 0) {\n")
    L.append("      tbl.innerHTML = '<div style=\"color:var(--muted)\">\u041d\u0438\u043a\u043e\u0433\u043e \u043d\u0435\u0442 \u043e\u043d\u043b\u0430\u0439\u043d \u26cf\ufe0f</div>';\n")
    L.append("      return;\n")
    L.append("    }\n")
    L.append("    const rows = filtered.map((p) => {\n")
    L.append("      const seconds = p.total_seconds || 0;\n")
    L.append("      const hours = Math.floor(seconds / 3600);\n")
    L.append("      const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';\n")
    L.append("      const isOnline = p.is_online ? '<span style=\"display:inline-block;width:8px;height:8px;background:#4caf50;border-radius:50%;margin-right:5px;vertical-align:middle\"></span>' : '';\n")
    L.append("      return `<div style=\"display:flex;flex-direction:column;align-items:center;gap:10px;background:var(--card,#1a1a1a);border:1px solid var(--border,#2a2a2a);border-radius:10px;padding:16px 12px;width:160px;flex-shrink:0;transition:border-color .2s;cursor:default;\" onmouseover=\"this.style.borderColor='#c8a84b'\" onmouseout=\"this.style.borderColor='var(--border,#2a2a2a)'\">\n")
    L.append("        <img src=\"${skinUrl}\" alt=\"${p.nickname}\" style=\"width:64px;height:64px;border-radius:6px;image-rendering:pixelated\" onerror=\"this.src='https://mc-heads.net/avatar/Steve/64'\">\n")
    L.append("        <div style=\"text-align:center\">\n")
    L.append("          <div style=\"font-weight:600;font-size:14px\">${isOnline}${p.nickname}</div>\n")
    L.append("          <div style=\"margin-top:6px;font-size:12px;color:var(--muted,#888)\">\u041d\u0430\u0438\u0433\u0440\u0430\u043b:</div>\n")
    L.append("          <div style=\"font-size:20px;font-weight:700;color:#c8a84b\">${hours > 0 ? hours + '\u0447' : '< 1\u0447'}</div>\n")
    L.append("        </div>\n")
    L.append("      </div>`;\n")
    L.append("    }).join('');\n")
    L.append("    tbl.innerHTML = '<div style=\"display:flex;flex-wrap:wrap;gap:12px;\">' + rows + '</div>';\n")
    L.append("  }\n\n")
    L.append("  async function loadStats() {\n")
    L.append("    try {\n")
    L.append("      const res = await fetch('/proxy.php?path=/web/server-stats');\n")
    L.append("      if (res.ok) {\n")
    L.append("        const data = await res.json();\n")
    L.append("        ['stat-online','nav-online','hero-online'].forEach(id => {\n")
    L.append("          const el = document.getElementById(id);\n")
    L.append("          if (el) el.textContent = id === 'nav-online' ? data.online + ' \u043e\u043d\u043b\u0430\u0439\u043d' : data.online;\n")
    L.append("        });\n")
    L.append("        const el = document.getElementById('stat-tps');\n")
    L.append("        if (el) el.textContent = data.tps;\n")
    L.append("      }\n")
    L.append("    } catch(e) { console.warn('server-stats error', e); }\n\n")
    L.append("    try {\n")
    L.append("      const res2 = await fetch('/proxy.php?path=/web/stats');\n")
    L.append("      if (res2.ok) {\n")
    L.append("        lastLeaders = await res2.json();\n")
    L.append("        renderStatsTable(lastLeaders);\n")
    L.append("      }\n")
    L.append("    } catch(e) { console.warn('stats error', e); }\n")
    L.append("  }\n\n")
    L.append("  let statsRefreshTimer = null;\n")
    L.append("  function startStatsRefresh() {\n")
    L.append("    if (statsRefreshTimer) clearInterval(statsRefreshTimer);\n")
    L.append("    statsRefreshTimer = setInterval(loadStats, 30000);\n")
    L.append("  }\n")
    L.append("  function stopStatsRefresh() {\n")
    L.append("    if (statsRefreshTimer) { clearInterval(statsRefreshTimer); statsRefreshTimer = null; }\n")
    L.append("  }\n\n")
    L.append("  // ---- COMMUNITIES ----\n")
    L.append("  async function loadCommunities() {\n")
    L.append("    try {\n")
    L.append("      const res = await fetch('/proxy.php?path=/web/communities');\n")
    L.append("      if (!res.ok) return;\n")
    L.append("      const list = await res.json();\n")
    L.append("      window._communities = list;\n")
    L.append("      const grid = document.getElementById('commGrid');\n")
    L.append("      if (!grid) return;\n")
    L.append("      if (!list || list.length === 0) {\n")
    L.append("        grid.innerHTML = '<div style=\"color:var(--muted)\">\u041e\u0431\u0449\u0438\u043d \u043f\u043e\u043a\u0430 \u043d\u0435\u0442. \u0421\u043e\u0437\u0434\u0430\u0439 \u043f\u0435\u0440\u0432\u0443\u044e!</div>';\n")
    L.append("        return;\n")
    L.append("      }\n")
    L.append("      const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n")
    L.append("      grid.innerHTML = list.map(c => `\n")
    L.append("        <div class=\"comm-card\">\n")
    L.append("          ${c.banner_url ? `<img src=\"${c.banner_url}\" style=\"width:100%;height:80px;object-fit:cover;border-radius:6px 6px 0 0;margin-bottom:8px\">` : ''}\n")
    L.append("          <div class=\"comm-top\">\n")
    L.append("            <span class=\"comm-icon\">${c.icon || '\U0001f3d8\ufe0f'}</span>\n")
    L.append("            <div>\n")
    L.append("              <div class=\"comm-name\">${c.name}</div>\n")
    L.append("              <div class=\"comm-members\">\U0001f465 ${c.member_count} \u0443\u0447\u0430\u0441\u0442\u043d\u0438\u043a\u043e\u0432</div>\n")
    L.append("            </div>\n")
    L.append("            ${user && user.id === c.owner_discord_id ? `<button onclick=\"openEditComm(${c.id})\" style=\"margin-left:auto;background:none;border:none;cursor:pointer;font-size:18px\">\u2699\ufe0f</button>` : ''}\n")
    L.append("          </div>\n")
    L.append("          <div class=\"comm-desc\">${c.description || ''}</div>\n")
    L.append("          <div class=\"comm-footer\">\n")
    L.append("            <span class=\"comm-tag\">${c.tag || ''}</span>\n")
    L.append("            <div style=\"display:flex;gap:6px\">\n")
    L.append("              ${c.discord_url ? `<a href=\"${c.discord_url}\" target=\"_blank\" class=\"btn btn-sm\">Discord</a>` : ''}\n")
    L.append("              <button class=\"comm-join\" onclick=\"joinCommunity(${c.id})\">\u0412\u0441\u0442\u0443\u043f\u0438\u0442\u044c</button>\n")
    L.append("            </div>\n")
    L.append("          </div>\n")
    L.append("        </div>`).join('');\n")
    L.append("    } catch(e) { console.warn('communities error', e); }\n")
    L.append("  }\n\n")
    L.append("  async function createCommunity() {\n")
    L.append("    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n")
    L.append("    if (!user) { openModal('loginModal'); return; }\n")
    L.append("    const name = document.getElementById('commName').value.trim();\n")
    L.append("    if (!name) { alert('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435'); return; }\n")
    L.append("    const res = await fetch('/proxy.php?path=/web/communities', {\n")
    L.append("      method: 'POST',\n")
    L.append("      headers: {'Content-Type':'application/json'},\n")
    L.append("      body: JSON.stringify({ name, discord_id: user.id })\n")
    L.append("    });\n")
    L.append("    const data = await res.json();\n")
    L.append("    if (!res.ok) { alert(data.detail || '\u041e\u0448\u0438\u0431\u043a\u0430'); return; }\n")
    L.append("    closeModal('createCommModal');\n")
    L.append("    document.getElementById('commName').value = '';\n")
    L.append("    loadCommunities();\n")
    L.append("  }\n\n")
    L.append("  async function joinCommunity(id) {\n")
    L.append("    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n")
    L.append("    if (!user) { openModal('loginModal'); return; }\n")
    L.append("    const res = await fetch('/proxy.php?path=/web/communities/' + id + '/join', {\n")
    L.append("      method: 'POST',\n")
    L.append("      headers: {'Content-Type':'application/json'},\n")
    L.append("      body: JSON.stringify({ discord_id: user.id })\n")
    L.append("    });\n")
    L.append("    const data = await res.json();\n")
    L.append("    if (!res.ok) { alert(data.detail || '\u041e\u0448\u0438\u0431\u043a\u0430'); return; }\n")
    L.append("    loadCommunities();\n")
    L.append("  }\n\n")
    L.append("  let editCommId = null;\n")
    L.append("  function openEditComm(id) {\n")
    L.append("    const c = (window._communities || []).find(x => x.id === id);\n")
    L.append("    editCommId = id;\n")
    L.append("    if (c) {\n")
    L.append("      document.getElementById('editCommName').value = c.name || '';\n")
    L.append("      document.getElementById('editCommIcon').value = c.icon || '';\n")
    L.append("      document.getElementById('editCommDesc').value = c.description || '';\n")
    L.append("      document.getElementById('editCommTag').value = c.tag || '';\n")
    L.append("      document.getElementById('editCommBanner').value = c.banner_url || '';\n")
    L.append("      document.getElementById('editCommDiscord').value = c.discord_url || '';\n")
    L.append("    }\n")
    L.append("    openModal('editCommModal');\n")
    L.append("  }\n\n")
    L.append("  async function saveCommEdit() {\n")
    L.append("    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n")
    L.append("    if (!user || !editCommId) return;\n")
    L.append("    const body = {\n")
    L.append("      discord_id: user.id,\n")
    L.append("      name: document.getElementById('editCommName').value.trim(),\n")
    L.append("      icon: document.getElementById('editCommIcon').value.trim(),\n")
    L.append("      description: document.getElementById('editCommDesc').value.trim(),\n")
    L.append("      tag: document.getElementById('editCommTag').value.trim(),\n")
    L.append("      banner_url: document.getElementById('editCommBanner').value.trim(),\n")
    L.append("      discord_url: document.getElementById('editCommDiscord').value.trim(),\n")
    L.append("    };\n")
    L.append("    const res = await fetch('/proxy.php?path=/web/communities/' + editCommId, {\n")
    L.append("      method: 'PATCH',\n")
    L.append("      headers: {'Content-Type':'application/json'},\n")
    L.append("      body: JSON.stringify(body)\n")
    L.append("    });\n")
    L.append("    if (!res.ok) { const d = await res.json(); alert(d.detail || '\u041e\u0448\u0438\u0431\u043a\u0430'); return; }\n")
    L.append("    closeModal('editCommModal');\n")
    L.append("    loadCommunities();\n")
    L.append("  }\n\n")
    L.append("  // \u0432\u044b\u0437\u043e\u0432 \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0435 \u043d\u0430 \u0432\u043a\u043b\u0430\u0434\u043a\u0438\n")
    L.append("  const _origShowPage = window.showPage;\n")
    L.append("  window.showPage = function(name) {\n")
    L.append("    if (_origShowPage) _origShowPage(name);\n")
    L.append("    if (name === 'stats') { loadStats(); startStatsRefresh(); }\n")
    L.append("    else { stopStatsRefresh(); }\n")
    L.append("    if (name === 'communities') loadCommunities();\n")
    L.append("  };\n\n")
    L.append("  loadStats();\n")
    L.append("  loadCommunities();")

    new_js = "".join(L)
    content = content[:line_start] + new_js + content[js_end:]
    print("OK: JS block replaced")

# ===== SAVE =====
with open("index_new.html", "w", encoding="utf-8") as f:
    f.write(content)

if errors:
    print("ERRORS:")
    for e in errors:
        print(" -", e)
else:
    print("All done. Saved to index_new.html")
    print("New file size:", len(content), "chars")
