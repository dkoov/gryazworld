'use strict';
const axios = require('axios');

const cache = new Map();

async function lookupGeo(ip) {
  if (ip === '127.0.0.1' || ip.startsWith('192.168.') || ip.startsWith('10.')) {
    return '🏠 Локалхост';
  }
  if (cache.has(ip)) return cache.get(ip);

  try {
    const { data } = await axios.get(
      `http://ip-api.com/json/${encodeURIComponent(ip)}?fields=status,country,city,countryCode`,
      { timeout: 4000 }
    );
    if (data.status !== 'success') return '🌐 Unknown';

    const flag   = toFlag(data.countryCode);
    const result = `${flag} **${data.country}**, ${data.city || '—'}`;
    cache.set(ip, result);
    setTimeout(() => cache.delete(ip), 12 * 3600 * 1000);
    return result;
  } catch (_) {
    return '🌐 Unknown';
  }
}

function toFlag(code) {
  if (!code || code.length !== 2) return '🌐';
  const off = 0x1F1E6 - 65;
  return String.fromCodePoint(code.charCodeAt(0) + off) +
         String.fromCodePoint(code.charCodeAt(1) + off);
}

module.exports = { lookupGeo };
