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

Prioritet 2 - UI/UX
- [ ] Animacije, Dark mode toggle, Splash screen

Prioritet 3 - Napredne funkcije
- [ ] Kalendar prikaz, statistike, slike lijekova, kategorije

Prioritet 4 - Sigurnost i stabilnost
- [ ] Error handling, logging, cloud backup

Prioritet 5 - Testiranje
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

*Zadnja izmjena: 28. Listopad 2025.*

