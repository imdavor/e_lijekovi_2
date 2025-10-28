# TODO za e-Lijekovi (Android)

## Verzija
- Trenutna verzija: 1.1.1 (versionCode: 3)
- Verzije i promjene prate se u CHANGELOG.md i VERSION_TRACKER.md

---

## Najnoviji uspjesi (prosinac 2024. – listopad 2025.)
- Napredni JPN Schedule Bar: vizualno praćenje terapijskih segmenata
- Pametno označavanje kartice: boja kartice mijenja se tek kad su svi segmenti uzeti
- Poboljšan kalendar layout: split view, veće točke, bolja preglednost
- Batch Schedule Update: optimiziran performance za grupno uzimanje
- Terapijsko grupiranje po rasporedu (jutro, podne, večer, ostalo)
- "Uzmi sve" funkcionalnost za terapijske grupe
- Intervalno doziranje (antibiotici, fleksibilni intervali, automatsko računanje sljedeće doze)
- Vizualno označavanje uzetih doza po danima
- Ručno sortiranje lijekova unutar grupe (drag & drop)
- Poboljšan UI: kompaktan prikaz, filteri, search bar
- Edit i unos lijekova s podrškom za sve bitne podatke
- Dodano: Snackbar "Poništi" (Undo) za brisanje lijekova — omogućava vraćanje obrisanog lijeka ako korisnik brzo odabere 'Poništi' (MainActivity)
- Svi glavni problemi iz prethodnih verzija su riješeni (ikonice, import/export, duplikati, serialization, build)

---

## Preostali zadaci / planirane nadogradnje

### 1. Poboljšanja intervalnog doziranja
- [x] Fleksibilan unos početka terapije (datum + vrijeme)
- [x] Automatsko računanje sljedeće doze kroz dane
- [ ] Notifikacije za vrijeme uzimanja
- [ ] Compliance tracking za intervalne lijekove (kasno uzimanje, preskočene doze)
- [ ] **Aktivirati funkcije uzimanja lijekova i praćenje stanja (ručno označavanje doze, ažuriranje trenutnoStanje, compliance zapis, pojedinačno i grupno uzimanje) — bez prikaza vremena zadnjeg uzimanja jer boja kartice to vizualno pokazuje**

### 1.1 Plan za funkcije uzimanja lijekova (pojedinačno i grupno)
- Dodati gumb (npr. "✓ Uzmi" ili ikona čeka) na svaku karticu lijeka u listi.
- Klikom na gumb:
    - Smanjuje se trenutnoStanje za 1 (ili za dozu, ako je definirana).
    - Za intervalne lijekove: zapisuje se compliance zapis (vrijeme, kasni li, preskočeno).
    - Za standardne lijekove: označava se doza za to doba dana kao uzeta (jutro/podne/večer).
    - Onemogućiti ponovno uzimanje za isti termin u istom danu (ili jasno vizualno prikazati da je već uzeto).
- Na vrhu svake grupe (jutro/podne/večer) prikazati gumb "Uzmi sve".
- Klikom na "Uzmi sve":
    - Poziva se funkcija uzimanja za sve lijekove u toj grupi koji još nisu uzeti za taj termin.
    - Svi relevantni lijekovi se ažuriraju (stanje, compliance, vizualno označavanje).
- Nakon uzimanja (pojedinačno ili grupno), kartica se vizualno označava (npr. promjena boje, checkmark, animacija).
- Onemogućiti uzimanje ako je trenutnoStanje 0 (prikaz upozorenja).
- Onemogućiti dvostruko uzimanje za isti termin.
- Prikazati poruku o uspjehu ili grešci (snackbar/toast).
- Za svako uzimanje upisati compliance zapis (datum, vrijeme, kasni li, preskočeno).
- Omogućiti kasnije prikaz statistike uzimanja.

### 2. Compliance i statistike
- [ ] Tjedni/mjesečni pregled adherencije (kalendar s oznakama)
- [ ] Postotak uzimanja po lijekovima (7/30 dana)
- [ ] Trend grafovi i streak counter

### 3. UI i funkcionalna poboljšanja
- [ ] Prikaz cijene lijeka (ako je upisana)
- [ ] Prikaz slike lijeka (opcionalno)
- [ ] Dodatni filteri i sortiranje
- [ ] Drag & Drop reordering funkcionalnost (dodatno testiranje i poboljšanja)
- [ ] Funkcionalnost brisanja lijekova (swipe to delete)
- [ ] Rješavanje grešaka u importu podataka
- [ ] Popravljanje konfliktnih komponenti (npr. MainActivity duplikati)
- [ ] Čišćenje minor upozorenja o nekorištenim parametrima

---

## Napomene
- Ovaj TODO se odnosi isključivo na Android aplikaciju e-Lijekovi.
- Sve glavne funkcionalnosti iz verzije 1.1.0 su stabilne i testirane.
- Za detalje o promjenama pogledati CHANGELOG.md i VERSION_TRACKER.md.
- Sljedeća verzija (1.1.1) bit će fokusirana na bugfixeve i stabilnost.
- 
- 
  Želite li da odmah:
  ispravim ta tri upozorenja (preimenujem granted u _, pojednostavim ProductCard.kt, i riješim deprecated statusBarColor u Theme.kt) i ponovno pokrenem build? (Preporučam — male čiste izmjene, brzo se testiraju.)
  ili nastavimo s nekim drugim zadatkom (npr. implementacija local persistence, poboljšanje notifikacija, ili naplata/animacije)?
  Reci što želiš da napravim dalje — mogu odmah primijeniti i testirati izmjene.