path = 'C:/Users/IrMine/Desktop/grworld/index.html'
with open(path, 'r', encoding='utf-8') as f:
    html = f.read()

# ---- 1а. Удалить строку "Ванильный выживательный сервер · Java Edition" ----
old = '\n\n        <p>Ванильный выживательный сервер · Java Edition</p>\n'
if old in html:
    html = html.replace(old, '\n', 1)
    print('OK: 1a удалена строка Java Edition')
else:
    print('FAIL: 1a не найдена строка Java Edition')

# ---- 1б. Удалить весь блок stats-grid ----
old = """

    <div class="stats-grid">

      <div class="sstat"><div class="sstat-v online" id="stat-online">0</div><div class="sstat-l">Игроков онлайн</div></div>

      <div class="sstat"><div class="sstat-v" id="stat-max">20</div><div class="sstat-l">Максимум слотов</div></div>

      <div class="sstat"><div class="sstat-v" id="stat-tps">20.0</div><div class="sstat-l">TPS сервера</div></div>

      <div class="sstat"><div class="sstat-v">Java</div><div class="sstat-l">Версия</div></div>

    </div>"""
if old in html:
    html = html.replace(old, '', 1)
    print('OK: 1б удалён stats-grid')
else:
    print('FAIL: 1б stats-grid не найден')

# ---- 1в. Удалить блок "Игроки в сети" + playersList ----
old = """


    <div style="margin-bottom:16px">

      <div class="section-label" style="margin-bottom:12px">Игроки в сети</div>

      <div id="playersList"><div class="no-players">Сейчас никого нет онлайн. Станьте первым! \u26cf\ufe0f</div></div>

    </div>"""
if old in html:
    html = html.replace(old, '', 1)
    print('OK: 1в удалён playersList')
else:
    print('FAIL: 1в playersList не найден')

# ---- 1г. Удалить заголовок "Таблица лидеров" ----
old = '      <div class="section-label" style="margin-bottom:12px">Таблица лидеров</div>\n'
if old in html:
    html = html.replace(old, '', 1)
    print('OK: 1г удалён заголовок Таблица лидеров')
else:
    print('FAIL: 1г заголовок не найден')

# ---- 2. Заменить генерацию карточек в renderStatsTable ----
old_cards = r"""  const rows = filtered.map((p) => {
    const hours = Math.floor((p.total_seconds || 0) / 3600);
    const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';
    const isOnline = p.is_online ? '<span style="display:inline-block;width:8px;height:8px;background:#4caf50;border-radius:50%;margin-right:5px;vertical-align:middle"></span>' : '';
    return `<div style="display:flex;flex-direction:column;align-items:center;gap:10px;background:var(--card,#1a1a1a);border:1px solid var(--border,#2a2a2a);border-radius:10px;padding:16px 12px;width:160px;flex-shrink:0;transition:border-color .2s;cursor:default;" onmouseover="this.style.borderColor='#c8a84b'" onmouseout="this.style.borderColor='var(--border,#2a2a2a)'">
      <img src="${skinUrl}" alt="${p.nickname}" style="width:64px;height:64px;border-radius:6px;image-rendering:pixelated" onerror="this.src='https://mc-heads.net/avatar/Steve/64'">
      <div style="text-align:center">
        <div style="font-weight:600;font-size:14px">${isOnline}${p.nickname}</div>
        <div style="margin-top:6px;font-size:12px;color:var(--muted,#888)">Наиграл:</div>
        <div style="font-size:20px;font-weight:700;color:#c8a84b">${hours}ч</div>
      </div>
    </div>`;
  }).join('');
  tbl.innerHTML = '<div style="display:flex;flex-wrap:wrap;gap:12px;">' + rows + '</div>';"""

new_cards = r"""  const rows = filtered.map((p) => {
    const hours = Math.floor((p.total_seconds || 0) / 3600);
    const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';
    const isOnline = p.is_online ? '<span style="display:inline-block;width:8px;height:8px;background:#4caf50;border-radius:50%;margin-right:5px;vertical-align:middle"></span>' : '';
    return `<div style="
    display:flex;align-items:center;gap:12px;
    background:var(--card,#1a1a1a);
    border:1px solid var(--border,#2a2a2a);
    border-radius:10px;padding:10px 14px;
    min-width:200px;flex:1;max-width:260px;
    transition:border-color .2s;cursor:default;
  " onmouseover="this.style.borderColor='#c8a84b'" onmouseout="this.style.borderColor='var(--border,#2a2a2a)'">
    <img src="${skinUrl}" alt="${p.nickname}"
      style="width:48px;height:48px;border-radius:6px;image-rendering:pixelated;flex-shrink:0"
      onerror="this.src='https://mc-heads.net/avatar/Steve/64'">
    <div>
      <div style="font-weight:600;font-size:14px">${isOnline}${p.nickname}</div>
      <div style="font-size:12px;color:var(--muted,#888);margin-top:2px">Наиграл: <span style="color:#c8a84b;font-weight:700">${hours} ч.</span></div>
    </div>
  </div>`;
  }).join('');
  tbl.innerHTML = '<div style="display:flex;flex-wrap:wrap;gap:10px;">' + rows + '</div>';"""

if old_cards in html:
    html = html.replace(old_cards, new_cards, 1)
    print('OK: 2 карточки заменены')
else:
    print('FAIL: 2 старые карточки не найдены')

with open(path, 'w', encoding='utf-8') as f:
    f.write(html)
print('Файл сохранён.')
