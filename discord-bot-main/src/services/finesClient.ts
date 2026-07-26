const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:8000";
const PLUGIN_SECRET = process.env.PLUGIN_SECRET ?? "";

/** Полицейский вручную подтверждает оплату штрафа (кнопка в Discord). */
export async function markFinePaid(fineId: number): Promise<void> {
  const res = await fetch(`${BACKEND_URL}/mc/fines/mark-paid`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Plugin-Secret": PLUGIN_SECRET,
    },
    body: JSON.stringify({ fine_id: fineId }),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Backend ${res.status}: ${text}`);
  }
}

/** Полицейский вручную отмечает штраф неоплаченным (кнопка в Discord) -- выдаёт варн. */
export async function markFineUnpaid(fineId: number): Promise<{ player: string; total_warns: number }> {
  const res = await fetch(`${BACKEND_URL}/mc/fines/mark-unpaid`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Plugin-Secret": PLUGIN_SECRET,
    },
    body: JSON.stringify({ fine_id: fineId }),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Backend ${res.status}: ${text}`);
  }
  return res.json();
}
