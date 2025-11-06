# e-LijekoviHR — Dokumentacija (Konzolidirano)

Ovo je jedinstveno mjesto za praćenje TODO lista, dokumentacije i verzioniranja.
Sadrži: Version Tracker, Changelog, Upute za korištenje, Implementacijske bilješke i TODO (prioriteti).

---

## Trenutna verzija
- Verzija: 1.1.1 (versionCode: 3)

---

## Sažetak projekta
Aplikacija `e-LijekoviHR` je stabilna i većina osnovnih funkcionalnosti je implementirana: dodavanje/uređivanje lijekova, grupiranje po terminima (Jutro/Popodne/Večer), export/import JSON, moderni UI (Material 3) i osnovna automatska perzistencija.

---

## Version Tracker & Changelog (kratko)
### [1.1.1] - 2025-10-28
**Added**
- Snackbar "Poništi" (Undo) nakon brisanja lijeka

**Changed**
- Podignut `targetSdk` na 36
- Ažurirana biblioteka `com.google.android.material` na 1.13.0

**Fixed**
- Manji refaktori i dokumentacijske izmjene

### [1.1.0] - 2024-12-14
- Schedule tracking, vizualizacija terapijskih segmenata, "Uzmi sve", intervalno doziranje, drag & drop (implementirano)

### [1.0.0]
- Početna verzija — osnovna funkcionalnost praćenja lijekova i JSON persistencija

> Detaljan changelog i pravila verzioniranja nalaze se u ovoj dokumentaciji (sekcija Roadmap & Verzioniranje).

---

## Upute za korištenje (kratko)
1) Dodavanje lijeka: FAB (+) → unesi naziv, dozu, odaberi termine → Spremi
2) Uređivanje: Edit ikona na kartici → izmijeni → Spremi
3) Brisanje: Delete ikona (Undo snackbar dostupan nakon brisanja)
4) Export/Import: Postavke → Upravljanje podacima → Export/Import JSON

---

## Implementacija — ključne stavke
- Model: `Lijek.kt`, enum `DobaDana`
- UI: Jetpack Compose + Material 3
- Export/Import: kotlinx.serialization
- Automatsko spremanje pri izmjenama (trenutna implementacija: files + auto-save; preporuka: Room za finalnu verziju)

---

## TODO (konsolidirano, prioriteti)
Prioritet 1 - Osnovno
- [ ] Lokalno spremanje podataka (Room / DataStore ako nije finalno)
- [ ] Brisanje lijeka (swipe to delete + potvrda i Undo)
- [ ] Dodavanje pakiranja (gumb u kartici)
- [ ] Search / Filter / Sortiranje
- [ ] Notifikacije za uzimanje (WorkManager, snooze, akcije)

Prioritet 1 - Implementirano / popravci (trenutno)
- [x] Postavke su sada scrollable (fiks u `SettingsScreen`) — korisnički problem: ekran nije bio skrolabilan
- [x] Privremeno rješenje za ponoćni reset (`MidnightResetReceiver`): prilikom resetiranja brišu se `dozeZaDan` i zapisi iz `complianceHistory` koji su datirani na "danas" se premještaju na "jučer" kako bi se kartice odblokirale (smanjuje broj hitnih bugfixa).  
  Napomena: ovo je quick-fix koji mijenja povijest; vidjeti Prioritet 2 — bolja / transparentnija strategija.

Prioritet 2 - UX/behavioral fixes (preporučeno nakon stabilizacije)
- [ ] Implementirati timestamp-based reset (preporuka): ne mijenjati `complianceHistory` datum, nego koristiti `last_daily_reset` i/ili `createdAtMillis` u zapisima kako bi se korektno odlučivalo što pripada današnjem danu. Ovo čuva točan zapis kada je korisnik kliknuo, a istovremeno omogućava ispravno otključavanje kartica.
- [ ] Podrška za točan reset u 00:00: istražiti i/ili tražiti od korisnika permission `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` ili alternativne mehanizme (WorkManager + exact alarm UX). Trenutno `NotificationScheduler` pokušava `setExactAndAllowWhileIdle` i pada na inexact `set()` ako nema permission.
- [ ] Poništi/Povratak uzimanja (ako je slučajno kliknuto): trenutno postoji Undo snackbar za pojedinačne operacije (na razini UI) — razmotriti i eksplicitnu opciju u detaljima lijeka za poništavanje zadnjeg uzimanja.
- [ ] Instant update cijene u dialogu (opcija A): osigurati da promjena cijene u edit dijalogu odmah ažurira `Ukupno za narudžbu` i sve izračune bez potrebe za prelaskom na drugi ekran.

Prioritet 3 - UI/UX
- [ ] Animacije, Dark mode toggle, Splash screen

Prioritet 4 - Napredne funkcije
- [ ] Kalendar prikaz, statistike, slike lijekova, kategorije

Prioritet 5 - Sigurnost i stabilnost
- [ ] Error handling, logging, cloud backup

Prioritet 6 - Testiranje
- [ ] Unit, Compose UI i integracijski testovi

---

## Roadmap
- 1.1.x: Bugfix i čišćenje warninga, swipe-to-delete
- 1.2.0: Drag & Drop poboljšanja, testovi
- 1.3.0+: Notifikacije, Calendar, Cloud backup

---

## Napomene
- Ovaj fajl je jedinstveno mjesto za dokumentaciju. Ostali `.md` u rootu ili `docs/archive/` su arhivirani ili preusmjereni na ovu stranicu.
- Ako želite da promijenim mjesto (npr. da canonical bude `PROJECT_DOCS.md` u rootu), javite i prenesem sadržaj.

---

*Zadnja izmjena: 05. Studeni 2025.*
