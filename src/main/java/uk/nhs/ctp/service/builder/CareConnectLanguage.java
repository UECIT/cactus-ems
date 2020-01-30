package uk.nhs.ctp.service.builder;

import lombok.Getter;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

@Getter
enum CareConnectLanguage {

  q1("Braille"),
  q2("American Sign Language"),
  q3("Australian Sign Language"),
  q4("British Sign Language"),
  q5("Makaton"),
  aa("Afar"),
  ab("Abkhazian"),
  af("Afrikaans"),
  ak("Akan"),
  sq("Albanian"),
  am("Amharic"),
  ar("Arabic"),
  an("Aragonese"),
  hy("Armenian"),
  as("Assamese"),
  av("Avaric"),
  ae("Avestan"),
  ay("Aymara"),
  az("Azerbaijani"),
  ba("Bashkir"),
  bm("Bambara"),
  eu("Basque"),
  be("Belarusian"),
  bn("Bengali"),
  bh("Bihari languages"),
  bi("Bislama"),
  bo("Tibetan"),
  bs("Bosnian"),
  br("Breton"),
  bg("Bulgarian"),
  my("Burmese"),
  ca("Catalan; Valencian"),
  cs("Czech"),
  ch("Chamorro"),
  ce("Chechen"),
  zh("Chinese"),
  cu("Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic"),
  cv("Chuvash"),
  kw("Cornish"),
  co("Corsican"),
  cr("Cree"),
  cy("Welsh"),
  da("Danish"),
  de("German"),
  dv("Divehi; Dhivehi; Maldivian"),
  nl("Dutch; Flemish"),
  dz("Dzongkha"),
  el("Greek, Modern (1453-)"),
  en("English"),
  eo("Esperanto"),
  et("Estonian"),
  ee("Ewe"),
  fo("Faroese"),
  fa("Persian"),
  fj("Fijian"),
  fi("Finnish"),
  fr("French"),
  fy("Western Frisian"),
  ff("Fulah"),
  ka("Georgian"),
  gd("Gaelic; Scottish Gaelic"),
  ga("Irish"),
  gl("Galician"),
  gv("Manx"),
  gn("Guarani"),
  gu("Gujarati"),
  ht("Haitian; Haitian Creole"),
  ha("Hausa"),
  he("Hebrew"),
  hz("Herero"),
  hi("Hindi"),
  ho("Hiri Motu"),
  hr("Croatian"),
  hu("Hungarian"),
  ig("Igbo"),
  is("Icelandic"),
  io("Ido"),
  ii("Sichuan Yi; Nuosu"),
  iu("Inuktitut"),
  ie("Interlingue; Occidental"),
  ia("Interlingua (International Auxiliary Language Association)"),
  id("Indonesian"),
  ik("Inupiaq"),
  it("Italian"),
  jv("Javanese"),
  ja("Japanese"),
  kl("Kalaallisut; Greenlandic"),
  kn("Kannada"),
  ks("Kashmiri"),
  kr("Kanuri"),
  kk("Kazakh"),
  km("Central Khmer"),
  ki("Kikuyu; Gikuyu"),
  rw("Kinyarwanda"),
  ky("Kirghiz; Kyrgyz"),
  kv("Komi"),
  kg("Kongo"),
  ko("Korean"),
  kj("Kuanyama; Kwanyama"),
  ku("Kurdish"),
  lo("Lao"),
  la("Latin"),
  lv("Latvian"),
  li("Limburgan; Limburger; Limburgish"),
  ln("Lingala"),
  lt("Lithuanian"),
  lb("Luxembourgish; Letzeburgesch"),
  lu("Luba-Katanga"),
  lg("Ganda"),
  mk("Macedonian"),
  mh("Marshallese"),
  ml("Malayalam"),
  mi("Maori"),
  mr("Marathi"),
  ms("Malay"),
  mg("Malagasy"),
  mt("Maltese"),
  mn("Mongolian"),
  na("Nauru"),
  nv("Navajo; Navaho"),
  nr("Ndebele, South; South Ndebele"),
  nd("Ndebele, North; North Ndebele"),
  ng("Ndonga"),
  ne("Nepali"),
  nn("Norwegian Nynorsk; Nynorsk, Norwegian"),
  nb("Bokmal, Norwegian; Norwegian Bokmal"),
  no("Norwegian"),
  ny("Chichewa; Chewa; Nyanja"),
  oc("Occitan (post1500)"),
  oj("Ojibwa"),
  or("Oriya"),
  om("Oromo"),
  os("Ossetian; Ossetic"),
  pa("Panjabi; Punjabi"),
  pi("Pali"),
  pl("Polish"),
  pt("Portuguese"),
  ps("Pushto; Pashto"),
  qu("Quechua"),
  rm("Romansh"),
  ro("Romanian; Moldavian; Moldovan"),
  rn("Rundi"),
  ru("Russian"),
  sg("Sango"),
  sa("Sanskrit"),
  sr("Serbian"),
  si("Sinhala; Sinhalese"),
  sk("Slovak"),
  sl("Slovenian"),
  se("Northern Sami"),
  sm("Samoan"),
  sn("Shona"),
  sd("Sindhi"),
  so("Somali"),
  st("Sotho, Southern"),
  es("Spanish; Castilian"),
  sc("Sardinian"),
  ss("Swati"),
  su("Sundanese"),
  sw("Swahili"),
  sv("Swedish"),
  ty("Tahitian"),
  ta("Tamil"),
  tt("Tatar"),
  te("Telugu"),
  tg("Tajik"),
  tl("Tagalog"),
  th("Thai"),
  ti("Tigrinya"),
  to("Tonga (Tonga Islands)"),
  tn("Tswana"),
  ts("Tsonga"),
  tk("Turkmen"),
  tr("Turkish"),
  tw("Twi"),
  ug("Uighur; Uyghur"),
  uk("Ukrainian"),
  ur("Urdu"),
  uz("Uzbek"),
  ve("Venda"),
  vi("Vietnamese"),
  vo("Volapuk"),
  wa("Walloon"),
  wo("Wolof"),
  xh("Xhosa"),
  yi("Yiddish"),
  yo("Yoruba"),
  za("Zhuang; Chuang"),
  zu("Zulu");

  private static final String SYSTEM = "https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-HumanLanguage-1";

  private String display;

  CareConnectLanguage(String display) {
    this.display = display;
  }

  public CodeableConcept toCodeableConcept() {
    return new CodeableConcept().addCoding(new Coding()
        .setCode(name())
        .setDisplay(display)
        .setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-HumanLanguage-1")
    );
  }
}