(use '[leiningen.exec :only (deps)])

(deps '[[org.clojure/tools.cli "0.2.2"]
        [bytebuffer "0.2.0"]])

(import 'java.io.File)
(import 'java.io.FileOutputStream)
(use 'clojure.java.io)
(use 'clojure.string)
(use '[clojure.tools.cli :only [cli]])
(use '[bytebuffer.buff :as bbb])
(require '[clojure.set :as set])
(require '[clojure.pprint :as pp])

(defmacro ?
  [val]
  `(let [x# ~val]
     (println '~val " is " x#)
     x#))

(defmacro do-let
  [[binding-form init-expr] & body]
  `(let [~binding-form ~init-expr]
     ~@body
     ~binding-form))

(def record-extraction-map
  {:SLEPDRIV {:starting-col 306, :field-len 1},
   :METVAL2_ {:starting-col 1589, :field-len 3},
   :_EDUCAG {:starting-col 1539, :field-len 1},
   :TNSARCNT {:starting-col 398, :field-len 1},
   :_VEGESUM {:starting-col 1575, :field-len 6},
   :_RFDRHV4 {:starting-col 1691, :field-len 1},
   :QLHLTH2 {:starting-col 270, :field-len 2},
   :_WT2RAKE {:starting-col 1040, :field-len 10},
   :FEETCHK {:starting-col 259, :field-len 2},
   :ARTHWGT {:starting-col 394, :field-len 1},
   :DRHPAD1 {:starting-col 309, :field-len 1},
   :DIRCONT1 {:starting-col 308, :field-len 1},
   :COPDTEST {:starting-col 404, :field-len 1},
   :ACEHURT {:starting-col 482, :field-len 1},
   :CHILDREN {:starting-col 119, :field-len 2},
   :NRECSEL {:starting-col 44, :field-len 6},
   :_RFDRMN4 {:starting-col 1692, :field-len 1},
   :ASTHMAGE {:starting-col 377, :field-len 2},
   :_MINACT1 {:starting-col 1620, :field-len 5},
   :_STATE {:starting-col 0, :field-len 2},
   :_DRDXAR1 {:starting-col 1492, :field-len 1},
   :PAVIGM1_ {:starting-col 1651, :field-len 5},
   :VIREDIF3 {:starting-col 290, :field-len 1},
   :HASYMP4 {:starting-col 328, :field-len 1},
   :STRENGTH {:starting-col 187, :field-len 3},
   :HIVTST6 {:starting-col 217, :field-len 1},
   :HOWLONG {:starting-col 339, :field-len 1},
   :CHCOCNCR {:starting-col 94, :field-len 1},
   :RREMTSM2 {:starting-col 433, :field-len 1},
   :ADFAIL {:starting-col 444, :field-len 2},
   :ACTINT1_ {:starting-col 1602, :field-len 1},
   :CIMEMLOS {:starting-col 452, :field-len 1},
   :PAFREQ2_ {:starting-col 1615, :field-len 5},
   :ASRCHKUP {:starting-col 384, :field-len 2},
   :ASINHALR {:starting-col 392, :field-len 1},
   :ARTHSOCL {:starting-col 194, :field-len 1},
   :SEQNO {:starting-col 32, :field-len 10},
   :HLTHPLN1 {:starting-col 79, :field-len 1},
   :CIINTFER {:starting-col 458, :field-len 1},
   :AVEDRNK2 {:starting-col 211, :field-len 2},
   :ASTHMED3 {:starting-col 391, :field-len 1},
   :MAXVO2_ {:starting-col 1592, :field-len 5},
   :ASDRVIST {:starting-col 382, :field-len 2},
   :EXEROFT1 {:starting-col 173, :field-len 3},
   :BPEATADV {:starting-col 319, :field-len 1},
   :HPVADVC2 {:starting-col 400, :field-len 1},
   :_FRT16 {:starting-col 1581, :field-len 1},
   :INCOME2 {:starting-col 123, :field-len 2},
   :_LLCPM02 {:starting-col 1441, :field-len 3},
   :VEGETAB1 {:starting-col 167, :field-len 3},
   :DROCDY3_ {:starting-col 1679, :field-len 3},
   :PCPSADIS {:starting-col 350, :field-len 1},
   :SMCPROGQ {:starting-col 361, :field-len 1},
   :LASTPAP2 {:starting-col 343, :field-len 1},
   :_LLCPWT {:starting-col 1474, :field-len 10},
   :SSBCALRI {:starting-col 278, :field-len 2},
   :GENHLTH {:starting-col 72, :field-len 1},
   :_CRACE {:starting-col 1249, :field-len 2},
   :_PA300R1 {:starting-col 1669, :field-len 1},
   :EXEROFT2 {:starting-col 181, :field-len 3},
   :_STRWT {:starting-col 1000, :field-len 10},
   :BPHI2MR {:starting-col 324, :field-len 1},
   :CHCSCNCR {:starting-col 93, :field-len 1},
   :CASTHDX2 {:starting-col 503, :field-len 1},
   :SLEPSNOR {:starting-col 303, :field-len 1},
   :_LLCPM01 {:starting-col 1438, :field-len 3},
   :HAVHPAD {:starting-col 310, :field-len 1},
   :PAMIN1_ {:starting-col 1636, :field-len 5},
   :VHDRPTSD {:starting-col 423, :field-len 1},
   :INTVID {:starting-col 24, :field-len 5},
   :SMOKE100 {:starting-col 101, :field-len 1},
   :NUMPHON2 {:starting-col 142, :field-len 1},
   :ASYMPTOM {:starting-col 389, :field-len 1},
   :_TOTINDA {:starting-col 1585, :field-len 1},
   :GPEMRCM1 {:starting-col 416, :field-len 1},
   :BPALCHOL {:starting-col 317, :field-len 1},
   :RCSBRACE {:starting-col 501, :field-len 1},
   :_RACEG2 {:starting-col 1512, :field-len 1},
   :SEX {:starting-col 150, :field-len 1},
   :ARTHEDU {:starting-col 396, :field-len 1},
   :STRSYMP6 {:starting-col 336, :field-len 1},
   :HAVARTH3 {:starting-col 96, :field-len 1},
   :CTYCODE1 {:starting-col 133, :field-len 3},
   :SMCMEDQT {:starting-col 363, :field-len 1},
   :_AIDTST3 {:starting-col 1694, :field-len 1},
   :_LLCPM05 {:starting-col 1450, :field-len 3},
   :_STSTR {:starting-col 995, :field-len 5},
   :SLEPTIME {:starting-col 301, :field-len 2},
   :LENGEXAM {:starting-col 341, :field-len 1},
   :REPDEPTH {:starting-col 12, :field-len 2},
   :_CLCPM03 {:starting-col 1368, :field-len 3},
   :_CLCPM02 {:starting-col 1365, :field-len 3},
   :QLACTLM2 {:starting-col 190, :field-len 1},
   :_PASTRNG {:starting-col 1671, :field-len 1},
   :SEATBELT {:starting-col 197, :field-len 1},
   :NUMMEN {:starting-col 64, :field-len 2},
   :ASATTACK {:starting-col 379, :field-len 1},
   :RCVFVCH4 {:starting-col 506, :field-len 6},
   :_RFDRWM4 {:starting-col 1693, :field-len 1},
   :PREGNANT {:starting-col 151, :field-len 1},
   :_LLCPM06 {:starting-col 1453, :field-len 3},
:VHTAKLIF {:starting-col 426, :field-len 1},
:PERSDOC2 {:starting-col 80, :field-len 1},
:FVGREEN {:starting-col 161, :field-len 3},
:IDAY {:starting-col 18, :field-len 2},
:CHOLCHK {:starting-col 86, :field-len 1},
:SMCCNSLQ {:starting-col 362, :field-len 1},
:PAMIN_ {:starting-col 1646, :field-len 5},
:EMTSUPRT {:starting-col 474, :field-len 1},
:_CLCPM01 {:starting-col 1362, :field-len 3},
:BPHIGH4 {:starting-col 83, :field-len 1},
:RCSRACE {:starting-col 495, :field-len 6},
:_LLCPM03 {:starting-col 1444, :field-len 3},
:ADANXEV {:starting-col 451, :field-len 1},
:_REGION {:starting-col 1050, :field-len 2},
:CHCCOPD {:starting-col 95, :field-len 1},
:MSCODE {:starting-col 994, :field-len 1},
:CIHOWOFT {:starting-col 456, :field-len 1},
:BEANDAY_ {:starting-col 1549, :field-len 4},
:MEDCOST {:starting-col 81, :field-len 1},
:HTM4 {:starting-col 1524, :field-len 3},
:ADDOWN {:starting-col 436, :field-len 2},
:CINOADLT {:starting-col 453, :field-len 1},
:CVDINFR4 {:starting-col 88, :field-len 1},
:ORACE2 {:starting-col 116, :field-len 1},
:LANDLINE {:starting-col 537, :field-len 1},
:CALLBACK {:starting-col 514, :field-len 1},
:PAMISS_ {:starting-col 1635, :field-len 1},
:PROFEXAM {:starting-col 340, :field-len 1},
:PCPSAREC {:starting-col 345, :field-len 1},
:_GEOSTR {:starting-col 2, :field-len 2},
:_RAWRAKE {:starting-col 1030, :field-len 10},
:AGE {:starting-col 107, :field-len 2},
:IMFVPLAC {:starting-col 205, :field-len 2},
:WHRTST9 {:starting-col 471, :field-len 2},
:ACEDIVRC {:starting-col 480, :field-len 1},
:_LLCPM04 {:starting-col 1447, :field-len 3},
:_RACE_G {:starting-col 1514, :field-len 1},
:FIRSTAID {:starting-col 337, :field-len 1},
:WRKHCF1 {:starting-col 307, :field-len 1},
:PRECALL {:starting-col 5, :field-len 1},
:_FLSHOT5 {:starting-col 1676, :field-len 1},
:CPDEMO1 {:starting-col 143, :field-len 1},
:CIRBIAGE {:starting-col 454, :field-len 2},
:ASPUNSAF {:starting-col 314, :field-len 1},
:_WT2 {:starting-col 1020, :field-len 10},
:ACEPUNCH {:starting-col 481, :field-len 1},
:FLUSHOT5 {:starting-col 198, :field-len 1},
:MISTMNT {:starting-col 450, :field-len 1},
:GPNOTEV1 {:starting-col 420, :field-len 2},
:LMTJOIN3 {:starting-col 192, :field-len 1},
:_LLCPM11 {:starting-col 1468, :field-len 3},
:GP3DYWTR {:starting-col 411, :field-len 1},
:PCPSADEC {:starting-col 351, :field-len 1},
:GRENDAY_ {:starting-col 1553, :field-len 4},
:_LLCPM10 {:starting-col 1465, :field-len 3},
:QSTLANG {:starting-col 892, :field-len 2},
:LASTSMK2 {:starting-col 104, :field-len 2},
:IMONTH {:starting-col 16, :field-len 2},
:CIMEDS {:starting-col 461, :field-len 1},
:PHYSHLTH {:starting-col 73, :field-len 2},
:_PAREC {:starting-col 1672, :field-len 1},
:CADULT {:starting-col 532, :field-len 1},
:SHSALOW1 {:starting-col 376, :field-len 1},
:PAVIGM2_ {:starting-col 1656, :field-len 5},
:ASNOSLEP {:starting-col 390, :field-len 1},
:GPBATRAD {:starting-col 414, :field-len 1},
:CSTATE {:starting-col 534, :field-len 1},
:FLUSHCH2 {:starting-col 505, :field-len 1},
:CHECKUP1 {:starting-col 82, :field-len 1},
:DIABETE3 {:starting-col 100, :field-len 1},
:CPDEMO3 {:starting-col 145, :field-len 1},
:CTELNUM1 {:starting-col 530, :field-len 1},
:CVDASPRN {:starting-col 313, :field-len 1},
:COPDQOL {:starting-col 405, :field-len 1},
:CHILDAGE {:starting-col 1271, :field-len 3},
:_VEGRESP {:starting-col 1568, :field-len 1},
:USEEQUIP {:starting-col 191, :field-len 1},
:DRNKANY5 {:starting-col 1678, :field-len 1},
:VICTRCT3 {:starting-col 296, :field-len 1},
:HPVADSHT {:starting-col 401, :field-len 2},
:EMPLOY {:starting-col 122, :field-len 1},
:DISPCODE {:starting-col 29, :field-len 3},
:CPDEMO2 {:starting-col 144, :field-len 1},
:HADHYST2 {:starting-col 344, :field-len 1},
:FC60_ {:starting-col 1597, :field-len 5},
:ADDEPEV2 {:starting-col 97, :field-len 1},
:HIVTSTD3 {:starting-col 218, :field-len 6},
:PFPVITMN {:starting-col 288, :field-len 1},
:BPMEDADV {:starting-col 323, :field-len 1},
:_LLCPM12 {:starting-col 1471, :field-len 3},
:ADTHINK {:starting-col 446, :field-len 2},
:ADLTCHLD {:starting-col 515, :field-len 1},
:CVDSTRK3 {:starting-col 90, :field-len 1},
:SCNTPAID {:starting-col 465, :field-len 1},
:CASTHNO2 {:starting-col 504, :field-len 1},
:CELLFON2 {:starting-col 531, :field-len 1},
:BPSLTADV {:starting-col 320, :field-len 1},
:_RFHYPE5 {:starting-col 1486, :field-len 1},
:MRACEORG {:starting-col 1495, :field-len 6},
:STRSYMP5 {:starting-col 335, :field-len 1},
:GPVACPL1 {:starting-col 418, :field-len 1},
:RCSGENDR {:starting-col 493, :field-len 1},
:VHDRTBI {:starting-col 424, :field-len 1},
:PFPPRGNT {:starting-col 281, :field-len 1},
:HADSGCO1 {:starting-col 356, :field-len 1},
:RRCOGNT2 {:starting-col 429, :field-len 1},
:STREHAB1 {:starting-col 312, :field-len 1},
:_VEGETEX {:starting-col 1584, :field-len 1},
:_RFSEAT3 {:starting-col 1675, :field-len 1},
:ALCDAY5 {:starting-col 208, :field-len 3},
:_CNRACE {:starting-col 1515, :field-len 1},
:FRUIT1 {:starting-col 155, :field-len 3},
:STRSYMP4 {:starting-col 334, :field-len 1},
:ADPLEASR {:starting-col 434, :field-len 2},
:SCNTLPAD {:starting-col 468, :field-len 1},
:CRACEASC {:starting-col 1243, :field-len 6},
:FMONTH {:starting-col 14, :field-len 2},
:EYEEXAM {:starting-col 261, :field-len 1},
:BPEATHBT {:starting-col 315, :field-len 1},
:ACEDRUGS {:starting-col 478, :field-len 1},
:WTKG3 {:starting-col 1527, :field-len 5},
:PADUR2_ {:starting-col 1607, :field-len 3},
:HISPANC2 {:starting-col 109, :field-len 1},
:_RFSEAT2 {:starting-col 1674, :field-len 1},
:CHCKIDNY {:starting-col 98, :field-len 1},
:CPDEMO4 {:starting-col 146, :field-len 3},
:TOLDHI2 {:starting-col 87, :field-len 1},
:CTELENUM {:starting-col 59, :field-len 1},
:_HCVU651 {:starting-col 1485, :field-len 1},
:RCHISLAT {:starting-col 494, :field-len 1},
:STRSYMP3 {:starting-col 333, :field-len 1},
:SCNTMEAL {:starting-col 464, :field-len 1},
:GPFLSLIT {:starting-col 415, :field-len 1},
:_MISFRTN {:starting-col 1565, :field-len 1},
:ARTTODAY {:starting-col 393, :field-len 1},
:ASERVIST {:starting-col 380, :field-len 2},
:_IMPRACE {:starting-col 1054, :field-len 2},
:GP3DYPRS {:starting-col 413, :field-len 1},
:CVDCRHD4 {:starting-col 89, :field-len 1},
:SMCPLANQ {:starting-col 365, :field-len 1},
:STRSYMP2 {:starting-col 332, :field-len 1},
:ORNGDAY_ {:starting-col 1557, :field-len 4},
:VEGEDA1_ {:starting-col 1561, :field-len 4},
:PDIABTST {:starting-col 244, :field-len 1},
:DIABEYE {:starting-col 262, :field-len 1},
:PAINACT2 {:starting-col 264, :field-len 2},
:ADENERGY {:starting-col 440, :field-len 2},
:HEIGHT3 {:starting-col 129, :field-len 4},
:GPEMRIN1 {:starting-col 417, :field-len 1},
:PSATIME {:starting-col 347, :field-len 1},
:SMOKDAY2 {:starting-col 102, :field-len 1},
:MENTHLTH {:starting-col 75, :field-len 2},
:HADSIGM3 {:starting-col 355, :field-len 1},
:RACE2 {:starting-col 1511, :field-len 1},
:PSATEST1 {:starting-col 346, :field-len 1},
:O_STATE {:starting-col 1063, :field-len 2},
:PFPPREPR {:starting-col 280, :field-len 1},
:SHINGLE1 {:starting-col 403, :field-len 1},
:_BMI5CAT {:starting-col 1536, :field-len 1},
:JOINPAIN {:starting-col 195, :field-len 2},
:ACETTHEM {:starting-col 485, :field-len 1},
:ACEDEPRS {:starting-col 476, :field-len 1},
:STRSYMP1 {:starting-col 331, :field-len 1},
:LSTBLDS3 {:starting-col 354, :field-len 1},
:EXERHMM2 {:starting-col 184, :field-len 3},
:SHSRIDEV {:starting-col 370, :field-len 2},
:RRPHYSM2 {:starting-col 432, :field-len 1},
:_RFBMI5 {:starting-col 1537, :field-len 1},
:PFPPRVNT {:starting-col 282, :field-len 1},
:QLSTRES2 {:starting-col 268, :field-len 2},
:PNEUVAC3 {:starting-col 207, :field-len 1},
:SMCQUITL {:starting-col 358, :field-len 1},
:GP3DYFD1 {:starting-col 412, :field-len 1},
:CHIMRCVE {:starting-col 512, :field-len 2},
:EXRACT01 {:starting-col 171, :field-len 2},
:_RAWCH {:starting-col 1251, :field-len 10},
:HADMAM {:starting-col 338, :field-len 1},
:ARTHDIS2 {:starting-col 193, :field-len 1},
:BPEXER {:starting-col 318, :field-len 1},
:SCNTMONY {:starting-col 463, :field-len 1},
:EXERHMM1 {:starting-col 176, :field-len 3},
:HAREHAB1 {:starting-col 311, :field-len 1},
:MARITAL {:starting-col 118, :field-len 1},
:CELLFON {:starting-col 60, :field-len 1},
:BPALCADV {:starting-col 321, :field-len 1},
:CHKHEMO3 {:starting-col 257, :field-len 2},
:_CASTHM1 {:starting-col 1490, :field-len 1},
:_DRNKMO4 {:starting-col 1687, :field-len 4},
:HASYMP1 {:starting-col 325, :field-len 1},
:PCPSAADV {:starting-col 349, :field-len 1},
:EXRACT02 {:starting-col 179, :field-len 2},
:RRATWRK2 {:starting-col 430, :field-len 1},
:_RFCHOL {:starting-col 1488, :field-len 1},
:NOBCUSE4 {:starting-col 285, :field-len 2},
:NATTMPTS {:starting-col 42, :field-len 2},
:PCPSARSN {:starting-col 348, :field-len 1},
:_AGE_G {:starting-col 1520, :field-len 1},
:VIGLUMA3 {:starting-col 297, :field-len 1},
:_FRUITEX {:starting-col 1583, :field-len 1},
:HIVRISK3 {:starting-col 224, :field-len 1},
:_RFBING5 {:starting-col 1682, :field-len 1},
:NRECSTR {:starting-col 50, :field-len 9},
:_DRNKDY4 {:starting-col 1683, :field-len 4},
:CIFAMCAR {:starting-col 459, :field-len 1},
:_DENSTR2 {:starting-col 4, :field-len 1},
:PREDIAB1 {:starting-col 245, :field-len 1},
:BPEXRADV {:starting-col 322, :field-len 1},
:SHSNHOM1 {:starting-col 368, :field-len 2},
:BLDSUGAR {:starting-col 249, :field-len 3},
:EXERANY2 {:starting-col 170, :field-len 1},
:GPWELPR3 {:starting-col 410, :field-len 1},
:SHSNWRK1 {:starting-col 366, :field-len 2},
:IYEAR {:starting-col 20, :field-len 4},
:ACETOUCH {:starting-col 484, :field-len 1},
:QSTVER {:starting-col 890, :field-len 2},
:ADEAT1 {:starting-col 442, :field-len 2},
:CIHCPROF {:starting-col 460, :field-len 1},
:WEIGHT2 {:starting-col 125, :field-len 4},
:RCSBIRTH {:starting-col 487, :field-len 6},
:_VEG23 {:starting-col 1582, :field-len 1},
:COPDMEDS {:starting-col 408, :field-len 2},
:RRHCARE3 {:starting-col 431, :field-len 1},
:_AGEG5YR {:starting-col 1517, :field-len 2},
:VHCOUNSL {:starting-col 425, :field-len 1},
:_PASTAER {:starting-col 1673, :field-len 1},
:SHSINPUB {:starting-col 372, :field-len 2},
:ASTHNOW {:starting-col 92, :field-len 1},
:TNSASHT1 {:starting-col 399, :field-len 1},
:METVAL1_ {:starting-col 1586, :field-len 3},
:COPDHOSP {:starting-col 407, :field-len 1},
:MRACE {:starting-col 110, :field-len 6},
:_IMPNPH {:starting-col 1056, :field-len 1},
:FVBEANS {:starting-col 158, :field-len 3},
:ASTHMA3 {:starting-col 91, :field-len 1},
:PADUR1_ {:starting-col 1604, :field-len 3},
:_LTASTH1 {:starting-col 1489, :field-len 1},
:FPCHLDF2 {:starting-col 287, :field-len 1},
:RENTHOM1 {:starting-col 149, :field-len 1},
:PROSTATE {:starting-col 352, :field-len 1},
:_IMPAGE {:starting-col 1052, :field-len 2},
:SSBFRUIT {:starting-col 275, :field-len 3},
:ARTHEXER {:starting-col 395, :field-len 1},
:VIPRFVS3 {:starting-col 291, :field-len 1},
:_CHOLCHK {:starting-col 1487, :field-len 1},
:SLEPDAY {:starting-col 304, :field-len 2},
:NUMWOMEN {:starting-col 66, :field-len 2},
:LASTSIG3 {:starting-col 357, :field-len 1},
:_AGE65YR {:starting-col 1519, :field-len 1},
:COPDDOC {:starting-col 406, :field-len 1},
:QLMENTL2 {:starting-col 266, :field-len 2},
:_MRACE {:starting-col 1509, :field-len 2},
:GPMNDEVC {:starting-col 419, :field-len 1},
:FRUITJU1 {:starting-col 152, :field-len 3},
:ACEPRISN {:starting-col 479, :field-len 1},
:FRUTDA1_ {:starting-col 1545, :field-len 4},
:STRFREQ_ {:starting-col 1630, :field-len 5},
:SHSHOMES {:starting-col 374, :field-len 1},
:HIVRDTS2 {:starting-col 473, :field-len 1},
:_BMI5 {:starting-col 1532, :field-len 4},
:_RFHLTH {:starting-col 1484, :field-len 1},
:_FRUTSUM {:starting-col 1569, :field-len 6},
:CHCVISON {:starting-col 99, :field-len 1},
:PAFREQ1_ {:starting-col 1610, :field-len 5},
:_LLCPM09 {:starting-col 1462, :field-len 3},
:_CHLDCNT {:starting-col 1538, :field-len 1},
:VIEYEXM3 {:starting-col 294, :field-len 1},
:FLSHTMY2 {:starting-col 199, :field-len 6},
:CIDIAGAZ {:starting-col 462, :field-len 1},
:_RACEGR2 {:starting-col 1513, :field-len 1},
:ACTINT2_ {:starting-col 1603, :field-len 1},
:IDATE {:starting-col 16, :field-len 8},
:INSULIN {:starting-col 248, :field-len 1},
:LSATISFY {:starting-col 475, :field-len 1},
:SCNTWRK1 {:starting-col 466, :field-len 2},
:PVTRESID {:starting-col 61, :field-len 1},
:_RFSMOK3 {:starting-col 1494, :field-len 1},
:PVTRESD2 {:starting-col 533, :field-len 1},
:_INCOMG {:starting-col 1540, :field-len 1},
:_CLLCPWT {:starting-col 1377, :field-len 10},
:SSBSUGAR {:starting-col 272, :field-len 3},
:_LLCPM08 {:starting-col 1459, :field-len 3},
:VIMACDG3 {:starting-col 298, :field-len 1},
:HTIN4 {:starting-col 1521, :field-len 3},
:STOPSMK2 {:starting-col 103, :field-len 1},
:PCTCELL {:starting-col 538, :field-len 3},
:VETERAN3 {:starting-col 117, :field-len 1},
:HASYMP6 {:starting-col 330, :field-len 1},
:REPNUM {:starting-col 6, :field-len 6},
:NUMADULT {:starting-col 62, :field-len 2},
:_MISVEGN {:starting-col 1566, :field-len 1},
:VHSUICID {:starting-col 427, :field-len 1},
:ADMOVE {:starting-col 448, :field-len 2},
:_RAW {:starting-col 1010, :field-len 10},
:PAMIN2_ {:starting-col 1641, :field-len 5},
:ADSLEEP {:starting-col 438, :field-len 2},
:_PNEUMO2 {:starting-col 1677, :field-len 1},
:EDUCA {:starting-col 121, :field-len 1},
:QLREST2 {:starting-col 299, :field-len 2},
:NUMHHOL2 {:starting-col 141, :field-len 1},
:ACESWEAR {:starting-col 483, :field-len 1},
:DRNK3GE5 {:starting-col 213, :field-len 2},
:BPMEDS {:starting-col 84, :field-len 1},
:_CNRACEC {:starting-col 1516, :field-len 1},
:ACEHVSEX {:starting-col 486, :field-len 1},
:_PRACE {:starting-col 1507, :field-len 2},
:ACEDRINK {:starting-col 477, :field-len 1},
:RSPSTATE {:starting-col 535, :field-len 2},
:SMCCALQT {:starting-col 360, :field-len 1},
:SMCTRYQT {:starting-col 359, :field-len 1},
:_LLCPM07 {:starting-col 1456, :field-len 3},
:_PAINDEX {:starting-col 1667, :field-len 1},
:DOCTDIAB {:starting-col 255, :field-len 2},
:SHSVHICL {:starting-col 375, :field-len 1},
:TYPCNTR6 {:starting-col 283, :field-len 2},
:CIASSIST {:starting-col 457, :field-len 1},
:_SMOKER3 {:starting-col 1493, :field-len 1},
:_CLCPM04 {:starting-col 1371, :field-len 3},
:RCSRLTN2 {:starting-col 502, :field-len 1},
:VHCOMBAT {:starting-col 422, :field-len 1},
:SMCTIMEQ {:starting-col 364, :field-len 1},
:_ASTHMS1 {:starting-col 1491, :field-len 1},
:VIINSUR3 {:starting-col 295, :field-len 1},
:PAVIGMN_ {:starting-col 1661, :field-len 5},
:_PACAT {:starting-col 1666, :field-len 1},
:HASYMP3 {:starting-col 327, :field-len 1},
:_PA150R1 {:starting-col 1668, :field-len 1},
:BLDSTOOL {:starting-col 353, :field-len 1},
:_WT2CH {:starting-col 1261, :field-len 10},
:VINOCRE3 {:starting-col 292, :field-len 2},
:_CLCPM05 {:starting-col 1374, :field-len 3},
:SCNTLWK1 {:starting-col 469, :field-len 2},
:TNSARCV {:starting-col 397, :field-len 1},
:FEETCHK2 {:starting-col 252, :field-len 3},
:VIDFCLT3 {:starting-col 289, :field-len 1},
:ASACTLIM {:starting-col 386, :field-len 3},
:_PA3002L {:starting-col 1670, :field-len 1},
:POORHLTH {:starting-col 77, :field-len 2},
:RRCLASS2 {:starting-col 428, :field-len 1},
:DIABEDU {:starting-col 263, :field-len 1},
:FVORANG {:starting-col 164, :field-len 3},
:HASYMP2 {:starting-col 326, :field-len 1},
:BLOODCHO {:starting-col 85, :field-len 1},
:USENOW3 {:starting-col 106, :field-len 1},
:MAXDRNKS {:starting-col 215, :field-len 2},
:DIABAGE2 {:starting-col 246, :field-len 2},
:BPSALT {:starting-col 316, :field-len 1},
:_MINACT2 {:starting-col 1625, :field-len 5},
:MRACEASC {:starting-col 1501, :field-len 6},
:FTJUDA1_ {:starting-col 1541, :field-len 4},
:HASYMP5 {:starting-col 329, :field-len 1},
:_PSU {:starting-col 32, :field-len 10},
:HADPAP2 {:starting-col 342, :field-len 1},
:_FRTRESP {:starting-col 1567, :field-len 1}})

(def not-asked-or-missing "Not asked or Missing")
(def dont-know-not-sure "Don´t Know/Not Sure")
(def dont-know-refused-missing "Don´t Know/Refused/Missing")

(def code-to-state-name
  {1  "Alabama"
   2  "Alaska"
   4  "Arizona"
   5  "Arkansas"
   6  "California"
   8  "Colorado"
   9  "Connecticut"
   10 "Delaware"
   11 "District of Columbia"
   12 "Florida"
   13 "Georgia"
   15 "Hawaii"
   16 "Idaho"
   17 "Illinois"
   18 "Indiana"
   19 "Iowa"
   20 "Kansas"
   21 "Kentucky"
   22 "Louisiana"
   23 "Maine"
   24 "Maryland"
   25 "Massachusetts"
   26 "Michigan"
   27 "Minnesota"
   28 "Mississippi"
   29 "Missouri"
   30 "Montana"
   31 "Nebraska"
   32 "Nevada"
   33 "New Hampshire"
   34 "New Jersey"
   35 "New Mexico"
   36 "New York"
   37 "North Carolina"
   38 "North Dakota"
   39 "Ohio"
   40 "Oklahoma"
   41 "Oregon"
   42 "Pennsylvania"
   44 "Rhode Island"
   45 "South Carolina"
   46 "South Dakota"
   47 "Tennessee"
   48 "Texas"
   49 "Utah"
   50 "Vermont"
   51 "Virginia"
   53 "Washington"
   54 "West Virginia"
   55 "Wisconsin"
   56 "Wyoming"
   66 "Guam"
   72 "Puerto Rico"})

(def code-to-yes-no-dont-know-refused
  {1 "Yes"
   2 "No"
   7 dont-know-not-sure
   9 "Refused"})

(defn parse-int
  [s]
  (Integer/parseInt s))
;  (try (Integer/parseInt s)
;    (catch NumberFormatException nfe nil)))

; BMIs are supposed to be coded with a 4 fixed pointer number with 2 implicit
; decimal places.  Some are miscoded with floating point instead, and the actual
; magnitude.  If < 100, assume it's mis-coded
(defn parse-bmi
  [s]
  (let [f (-> s
            (.replace " " ".")
            (Float/parseFloat))]
    (if (< 100 f) (int (* f 100)) f)))

(defn parse-numeric
  [s]
  (try (Integer/parseInt s)
    ; If at least some of s is numeric, try to parse as BMI, which may be a FP
    ; else just bail and pass up the NFE
    (catch NumberFormatException nfe
      (if (some #(Character/isDigit %) s) (parse-bmi s) (throw nfe)))))

; Some activity frequencies (which use the hundred's place as a code)
; are mis-formatted with blanks instead of a 0 between the hundreds and ones
; place
; Replace the " " with 0's
(defn parse-int-weird-chars-to-zeroes
  [s]
  (-> s
    (.replace " " "0")
    parse-int))

(defn decode-activity-frequency
  [n]
  (cond 
    (and (> n 100) (< n 200)) [(- n 100) :days-per-week]
    (and (> n 200) (< n 300)) [(- n 200) :days-in-past-30-days]
    :else nil))

(defn num-to-activity-frequency-or
  "Return a function that attempts to interpret a number as coded activity frequency
  If it can't interpret the number as a standard activity frequency encoding, it passes it to the user-provided cb for special value codings"
  [backup-fn]
  (fn [n]
    (if n
      (or (decode-activity-frequency n)
          (backup-fn n)) n)))

(def num-to-activity-frequency
  (num-to-activity-frequency-or (fn [_] nil)))

(defn activity-frequency-to-str
  [v]
  (str (v 0) (case (v 1)
               :days-per-week " days per week"
               :days-in-past-30-days "days in past 30 days")))

(def not-asked-or-missing "Not asked or Missing")
(def dont-know-not-sure "Don´t Know/Not Sure")
(def dont-know-refused-missing "Don´t Know/Refused/Missing")

(def basic-chronic-condition
  {:blank [nil not-asked-or-missing]
   :interp-fn parse-int
   :val-to-str code-to-yes-no-dont-know-refused})

(def all-relevant-fields
  ; General health
  {:GENHLTH {:desc "Would you say in general your health is"
             :blank [nil "Not asked or missing"]
             :val-to-str
             {1 "Excellent"
              2 "Very good"
              3 "Good"
              4 "Fair"
              5 "Poor"
              7 dont-know-not-sure
              9 "Refused"}}

   ; Demographics
   :_STATE {:desc "State FIPS Code"
            :val-to-str code-to-state-name}
   :EDUCA {:desc "What is the highest grade or year of school you completed?"
           :blank [nil "Not asked or missing"]
           :val-to-str
           {1 "Never attended school or only kindergarten"
            2 "Grades 1 through 8 (Elementary)"
            3 "Grades 9 through 11 (Some high school)"
            4 "Grade 12 or GED (High school graduate)"
            5 "College 1 year to 3 years (Some college or technical school)"
            6 "College 4 years or more (College graduate)"
            9 "Refused"}}
   :EMPLOY {:desc "Are you currenly (employment status)"
            :blank [nil "Not asked or missing"]
            :val-to-str
            {1 "Employed for wages"
             2 "Self-employed"
             3 "Out of work for more than 1 year"
             4 "Out of work for less that 1 year"
             5 "A homemaker"
             6 "A student"
             7 "Retired"
             8 "Unable to work"
             9 "Refused"}}
   :INCOME2 {:desc "Is your annual household income from all sources"
             :blank [nil "Not asked or missing"]
             :val-to-str 
             {1 "Less than $10,000"
              2 "Less than $15,000 ($10,000 to less than $15,000)"
              3 "Less than $20,000 ($15,000 to less than $20,000)"
              4 "Less than $25,000 ($20,000 to less than $25,000)"
              5 "Less than $35,000 ($25,000 to less than $35,000)"
              6 "Less than $50,000 ($35,000 to less than $50,000)"
              7 "Less than $75,000 ($50,000 to less than $75,000)"
              8 "$75,000 or more"
              77 "Don’t know/Not sure"
              99 "Refused"}}
   :ORACE2 {:desc "Which one of these groups would you say best represents your race?"
            :blank [nil "Not asked or missing"]
            :val-to-str
            {1 "White"
             2 "Black or African American"
             3 "Asian"
             4 "Native Hawaiian or Other Pacific Islander"
             5 "American Indian, Alaska Native"
             6 "Other"
             7 "Don’t know/Not sure"
             8 "Multiracial but preferred race not asked"
             9 "Refused"}}
   :AGE {:desc "What is your age?"
         :blank [nil not-asked-or-missing]
         :interp-fn (fn [n]
                      (cond n
                            (= n 7) :didnt-know-not-sure
                            (= n 9) :refused
                            (n < 18) nil
                            n))
         :val-to-str (fn [v]
                       (case v
                         :didnt-know-not-sure "Didn't know/Not sure"
                         :refused "Refused"
                         (str v " years old")))}
   ; behaviors
   :ALCDAY5 {:desc "During the past 30 days, how many days per week or per month did you have at least one drink of any alcoholic beverage such as beer, wine, a malt beverage or liquor?"
             :blank [nil "Not asked or missing"]
             :interp-fn num-to-activity-frequency
             :val-to-str activity-frequency-to-str}
;             :interp-fn
;             (str-to-activity-frequency-or
;               (fn [n]
;                 (cond
;                   (= n 777) :dont-know-not-sure
;                   (= n 888) :no-drinks-in-past-30-days)))
;             :val-to-str
;             (fn [v]
;               (cond
;                 (= v :no-drinks-in-past-30-days) "No drinks in past 30 days"
;                 (= v :dont-know-not-sure) dont-know-not-sure
;                 (vector? v) (activity-frequency-to-str v)))}

   :EXEROFT1 {:desc "How many times per week or per month did you take part in this activity during the past month?"
              :blank [nil not-asked-or-missing]
              :interp-fn num-to-activity-frequency
              :val-to-str activity-frequency-to-str}
;              :interp-fn (str-to-activity-frequency-or
;                           (fn [n]
;                             (cond
;                               (= n 777) :dont-know-not-sure
;                               (= n 999) :refused)))
;              :val-to-str 
;              (fn [v]
;                (cond
;                  (= v :dont-know-not-sure dont-know-not-sure
;                     (= v :refused) "Refused"
;                     (vector? v) (decode-activity-frequency v))))}
  :SMOKDAY2 {:desc "Do you now smoke cigarettes every day, some days, or not at all?"
             :blank [nil not-asked-or-missing]
             :val-to-str
             {1 "Every day"
              2 "Some days"
              3 "Not at all"
              7 dont-know-not-sure
              9 "Refused"}}

  :_BMI5 {:desc "Body Mass Index (BMI)"
          :blank [nil dont-know-refused-missing]
          ; Note, 2 implied decimal places
          :val-to-str str}

  ; Chronic conditions
  :DIABETE3 (update-in (assoc basic-chronic-condition
                              :desc "(Ever told) you have diabetes  (If 'Yes' and respondent is female, ask 'Was this only when you were pregnant?'.")
                       [:val-to-str]
                       assoc 
                       2 "Yes, but female told only during pregnancy"
                       4 "No, pre-diabetes or borderline diabetes")
  :CHCSCNCR (assoc basic-chronic-condition
                   :desc "(Ever told) you had skin cancer")
  :CHCOCNCR (assoc basic-chronic-condition
                   :desc "(Ever told) you had any other types of cancer?")
  :CVDINFR4 (assoc basic-chronic-condition
                   :desc "(Ever told) you had a heart attack, also called a myocardial infarction?")
  :CVDCRHD4 (assoc basic-chronic-condition
                   :desc "(Ever told) you had angina or coronary heart disease")
  :ADDEPEV2 (assoc basic-chronic-condition
                   :desc "(Ever told) you that you have a depressive disorder, including depression, major depression, dysthymia, or minor depression?")
  :HAVARTH3 (assoc basic-chronic-condition
                   :desc "(Ever told) you have some form of arthritis, rheumatoid arthritis, gout, lupus, or fibromyalgia?  (Arthritis diagnoses include: rheumatism, polymyalgia rheumatica; osteoarthritis (not osteporosis); tendonitis, bursitis, bunion, tennis elbow; carpal tunnel syndrome, tarsal tunnel syndrome; joint infection, etc.)")
  :CHCCOPD (assoc basic-chronic-condition
                  :desc "(Ever told) you have (COPD) chronic obstructive pulmonary disease, emphysema or chronic bronchitis?")})

(defn filter-vals
  [pred m]
  (into {} (filter #(pred (% 1)) m)))

(defn map-vals
  [f mp]
  (into {} (for [[k v] mp] [k (f v)])))

(defn filter-keys
  [pred m]
  (into {} (filter #(pred (% 0)) m)))

(def relevant-fields all-relevant-fields)
;  (filter-keys #{
                                   ;; :AGE
                                    ;:EXEROFT1 :ALCDAY6} all-relevant-fields))

(defn keyset-after-filter-vals
  [pred m]
  (->> m
    (filter-vals pred)
    keys
    (into #{})))

(def field-set 
  (into #{} (keys relevant-fields)))

; These functions are used for coalescing the code position tables into clojure
(defn chunk-seq-inner
  [xs]
  (let [next-3 (take 3 xs)
        [starting-col var-name field-len] next-3]
    (if (= 3 (count next-3))
      (cons [(keyword var-name) {:starting-col (- (Integer/parseInt starting-col) 1)
                                 :field-len (Integer/parseInt field-len)}]
            (lazy-seq (chunk-seq-inner (drop 3 xs))))
      [])))

(defn chunk-seq
  [xs]
  (chunk-seq-inner (drop 3 xs)))

(def parsed (cli *command-line-args*))

(def record-len 1695)

(defn buff-seq
  [rdr]
  (let [buff (char-array record-len)]
    (if (= record-len (.read rdr buff 0 record-len))
      (cons buff (lazy-seq (buff-seq rdr))) [])))

(def blank-num -1)

(defn buff-to-relevant-fields-numeric-records
  [buff]
  (into {}
        (for [[k v] relevant-fields
              :let [extractor (record-extraction-map k)
                    field (trim (apply str (for [idx 
                                                 (range (extractor :starting-col)
                                                        (+ (extractor :starting-col)
                                                           (extractor :field-len)))]
                                             (aget buff idx))))]]
          [k (if (= field "")
               blank-num
               (try (parse-numeric field)
                 (catch NumberFormatException nfe blank-num)))])))

(defn interpret-numeric-records
  [record]
  (into {} (for [[k v] record
                 :let [blnk (get-in relevant-fields [k :blank 0])
                       interp (get-in relevant-fields [k :interp-fn] identity)]]
             [k (if (= blank-num v) blnk (interp v))])))

(defn inc-counts-for-keys
  [counts xs]
  (reduce (fn [accum-counts k]
            (update-in accum-counts [k] inc)) counts xs))

(defn accum-record-validity
  [accum nxt]
  (let [nil-fields (keyset-after-filter-vals #(= :nil-error %) nxt)
        nfe-fields (keyset-after-filter-vals #(if (vector? %1)
                                                (= :nfe-error (%1 0))
                                                false) nxt)
        blank-fields (into #{} (for [[k v] nxt
                                     :when (=
                                             (get-in relevant-fields [k :blank 0])
                                             v)]
                                 k))
        invalid-fields (set/union nfe-fields nil-fields blank-fields)
        invalid-count (count invalid-fields)]
    (-> accum
      (update-in [:total-seen] inc)
      (update-in [:nfe-sets] (fn [field-map]
                               (reduce (fn [accum-map k]
                                         (update-in accum-map [k]
                                                    conj (nxt k)))
                                       field-map
                                       nfe-fields)))
      (update-in [:nfe-by-field-histo] inc-counts-for-keys nfe-fields)
      (update-in [:nil-by-field-histo] inc-counts-for-keys nil-fields)
      (update-in [:blank-by-field-histo] inc-counts-for-keys blank-fields)
      ; Basically no records were all valid
      ; (update-in [:total-with-all-valid] (if (= 0 blank-count) inc identity))
      ; TODO repetitive...
      (update-in [:nfe-error-per-record-histo (count nfe-fields)] inc)
      (update-in [:nil-error-per-record-histo (count nil-fields)] inc)
      (update-in [:blank-per-record-histo (count blank-fields)] inc)
      (update-in [:invalid-per-record-histo (count invalid-fields)] inc))))

(defn add-histogram-sum
  [histogram]
  (assoc histogram
         :total-count
         (reduce + 0 (for [[k v] histogram] v))))

(def field-count-histogram
  (into {}
        (map vector
             (range (+ 1 (count relevant-fields)))
             (repeat 0))))

(def field-histogram
  (into {} (map (fn [k] [k 0]) field-set)))

(def field-sets
  (into {} (map (fn [k] [k #{}]) field-set)))

(def field-extremes
  (into {} (map (fn [k] [k {:min Integer/MAX_VALUE
                            :max Integer/MIN_VALUE}]) field-set)))

(defn transform-to-common-invalids
  [rec]
  (into {} (for [[k v] rec]
             [k (if (or
                      (= :nfe-error v)
                      (= :nil-error v)
                      (= (get-in relevant-fields [k :blank 0]) v))
                  -1 v)])))

(defn summarize-record-validity
  [record-seq]
  (reduce
    accum-record-validity
    {:total-seen 0
     :nfe-sets field-sets
     ; Basically no records were all valid
     ; :total-with-all-valid 0
     :nfe-by-field-histo field-histogram
     :nil-by-field-histo field-histogram
     :blank-by-field-histo field-histogram
     :invalid-per-record-histo field-count-histogram
     :nfe-error-per-record-histo field-count-histogram
     :nil-error-per-record-histo field-count-histogram
     :blank-per-record-histo field-count-histogram}
    record-seq))

(defn accum-extremal-values
  [accum nxt]
  (into {} (for [[k old-v] accum]
             [k (-> old-v
                  (update-in [:min] min (nxt k))
                  (update-in [:max] max (nxt k)))])))

(defn extremal-values
  [rec-seq]
  (reduce accum-extremal-values field-extremes rec-seq))

(def numeric-types
  [{:min Byte/MIN_VALUE
    :max Byte/MAX_VALUE
    :name "sbyte" :fn bbb/put-byte :sz 1}
   {:min Short/MIN_VALUE
    :max Short/MAX_VALUE
    :name "sshort" :fn bbb/put-short :sz 2}
   {:min Integer/MIN_VALUE
    :max Integer/MAX_VALUE
    :name "sint" :fn bbb/put-int :sz 4}])

(defn find-smallest-containing-type
  [{mn :min mx :max}]
  (some (fn [{tmin :min tmax :max :as tp}]
          (if (and (<= mx tmax) (>= mn tmin)) tp nil))
        numeric-types))

(defn file-name-for-field
  [prefix kw out-type]
  (str prefix
       (-> kw
         name
         clojure.string/lower-case)
       "-"
       (out-type :name)))

(defn open-bbs-for-fields
  [numeric-seq out-types]
  (let [cnt (count numeric-seq)]
    (into {}
          (for [[k v] out-types]
            [k (bbb/byte-buffer (* (get-in out-types [k :sz]) cnt))]))))

(defn copy-numeric-seq-to-bbs
  [numeric-seq out-types]
  (do-let [bbs (open-bbs-for-fields numeric-seq out-types)]
          (doseq [rec numeric-seq [k v] rec]
            ((get-in out-types [k :fn]) (bbs k) v))
          (doseq [[k bb] bbs]
            (.flip bb))))

(defn write-out-bbs-for-fields
  [out-types bbs]
     (doseq [[k bb] bbs]
     (-> (file-name-for-field "raw-out/" k (out-types k))
       (File.)
       (FileOutputStream.)
       (.getChannel)
       (.write bb))))

(with-open [rdr (clojure.java.io/reader (get-in parsed [1 1]))]
  (let [numeric-seq (->> rdr
                      buff-seq
                      (take 1000)
                      (map buff-to-relevant-fields-numeric-records))
        out-types (map-vals find-smallest-containing-type
                            (extremal-values numeric-seq))
        bbs (copy-numeric-seq-to-bbs numeric-seq out-types)]
    (write-out-bbs-for-fields out-types bbs)
  ;  (pp/pprint extremes)
    ))

; ; Chunk up a list of col positions/codes/lengths into clojure table
; (with-open [rdr (clojure.java.io/reader (get-in parsed [1 1]))]
;   (pp/pprint
;     (into {} (chunk-seq (line-seq rdr)))))
