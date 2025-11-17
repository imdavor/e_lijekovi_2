# e-LijekoviHR — Upute (kratko)

Ovaj dokument daje kratak pregled što aplikacija sadrži i kako je koristiti.

## Što aplikacija sadrži
- Praćenje lijekova: dodavanje, uređivanje i brisanje lijekova.
- Grupiranje po terminima: Jutro / Popodne / Večer.
- Dodatne funkcionalnosti: "Uzmi sve", intervalno doziranje, drag & drop.
- Export / Import podataka u JSON formatu (korištenjem kotlinx.serialization).
- UI: Jetpack Compose + Material 3.
- Trenutna pohrana: files + automatsko spremanje (preporuka: Room za finalnu verziju).

## Brze upute za korištenje
1. Dodavanje lijeka:
   - Pritisnite FAB (+) → unesite naziv, dozu i odaberite termine → Spremi.
2. Uređivanje lijeka:
   - Pritisnite ikonu za uređivanje na kartici → izmijenite podatke → Spremi.
3. Brisanje lijeka:
   - Pritisnite ikonu za brisanje; nakon brisanja pojavljuje se Undo (Snackbar).
4. Export / Import:
   - Postavke → Upravljanje podacima → Export/Import JSON.

## Kratki pregled važnih implementacijskih elemenata
- Model: Lijek.kt i enum DobaDana.
- Persistencija: trenutačno files + auto-save; preporuka: migrirati na Room/DataStore.
- Notifikacije i raspoređivanje: planirano kroz WorkManager / exact alarms.

## Hitne preporuke (kratko)
- Prioritet 1: stabilna lokalna pohrana (Room), swipe-to-delete s potvrdom + Undo, pretraga/filtriranje.
- Prioritet 2: bolji reset dnevnih statusa (timestamp-based), poboljšati notifikacijski scheduler.
- Prioritet 3+: UX poboljšanja (animacije, dark toggle), napredne značajke (kalendar, statistike), sigurnost i testiranje.

## Gdje pronaći detalje
- Detaljan changelog, roadmap i TODO nalaze se u originalnom docs/README.md (kanon).
- Ako želite potpunu verziju dokumentacije, pogledajte: I:\PythonLab\e_lijekovi_2\docs\README.md

...existing code...

