# CHANGELOG - e-Lijekovi App

Sve značajne izmene u ovom projektu će biti dokumentovane u ovom fajlu.

Format se zasniva na [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
i ovaj projekt prati [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - U pripremi
### Planirana poboljšanja:
- [ ] Drag & Drop reordering funkcionalnost
- [ ] Rešavanje grešaka u importu podataka
- [ ] Funkcionalnost brisanja lijekova
- [ ] Popravljanje konfliktnih komponenti (MainActivity duplikati)

## [1.1.1] - 2025-10-28
### Added
- ✅ Snackbar "Poništi" (Undo) nakon brisanja lijeka — omogućava brzo vraćanje obrisanog zapisa (MainActivity)

### Changed
- 🔼 Podižen `targetSdk` na 36
- 🔼 Ažurirana biblioteka `com.google.android.material` na 1.13.0

### Fixed
- ✅ Manji refaktori i dokumentacijske izmjene

## [1.1.0] - 2024-12-14
### Added - Nove funkcionalnosti
- ✅ Schedule Tracking Sistem sa JPN (Jutro-Popodne-Navečer) indikatorima
- ✅ Napredni vizualni prikaz terapijskih segmenata (crveno/zeleno/bijelo)
- ✅ Pametno označavanje kartice (tek kad su SVI segmenti uzeti)
- ✅ Poboljšan kalendar layout sa split view dizajnom
- ✅ Batch Schedule Update za optimizovan performance
- ✅ Terapijsko grupiranje po rasporedu uzimanja
- ✅ "Uzmi sve" funkcionalnost za grupno uzimanje doze
- ✅ Intervalsko doziranje za antibiotike sa vremenskim intervalima
- ✅ UI optimizacija sa smanjenim razmacima
- ✅ Dnevno praćenje uzimanja doze sa checkbox indikatorima
- ✅ Drag & Drop sortiranje lijekova

### Fixed - Ispravke grešaka
- ✅ Popravljen prop passing između komponenti u edit sistemu
- ✅ JSON persistencija sada radi ispravno
- ✅ Razrešen konflikt između različitih App.js datoteka

### Changed - Izmene
- ✅ Header padding smanjen za 60%
- ✅ Footer potpuno uklonjen iz UI-ja
- ✅ Lista spacing optimizovan za 80% manje prostora
- ✅ Kompaktni search bar sa filter gumbovima

## [1.0.0] - Početna verzija
### Added
- Osnovna funkcionalnost praćenja lijekova
- Osnovni kalendar prikaz
- JSON data persistence
- Material Design UI

---

## Verzioniranje Pravila

### MAJOR verzija (X.0.0)
Uvećava se kada:
- Potpuno nova arhitektura aplikacije
- Breaking changes u data strukturi
- Nova glavna funkcionalnost koja menja core workflow

### MINOR verzija (1.X.0) 
Uvećava se kada:
- Nove funkcionalnosti (Schedule tracking, Drag&Drop, itd.)
- Značajna poboljšanja UI/UX-a
- Nova API integracija
- Dodavanje novih modula/komponenti

### PATCH verzija (1.1.X)
Uvećava se kada:
- Bug fixes i hotfixovi
- Manje UI tweakove
- Performance optimizacije
- Sigurnosne ispravke
- Refactoring bez funkcionalnih izmena

### versionCode (za Google Play)
- Uvek se uvećava za 1 sa svakim build-om
- Nezavisan od semantic versioning
- Trenutno: versionCode = 3 za verziju 1.1.1
