numberOfRecentHypoglycemias(1, _, _ ).
numberOfRecentHyperglycemias(1, _, _). 
userAge(20). 
userWeight(70).
userHeight(175).
hasDisease(_).
isSick.

lastValue(glucose, 120).
lastValue(insulin, 100).
lastValue(bloodPressure, 70).
lastValue(carboHydrate, 120).
lastValue(cholesterol, 90).
lastValue(weight, 70).
lastValue(hbA1c, 8).

timeSinceLastRegistry(insulin, 10).
timeSinceLastRegistry(exercise, 40).
timeSinceLastRegistry(glucose, 10).
timeSinceLastRegistry(bloodPressure, 200).
timeSinceLastRegistry(carboHydrate, 10).
timeSinceLastRegistry(cholesterol, 200).
timeSinceLastRegistry(weight, 10).
timeSinceLastRegistry(hbA1c, 200).

numberOfRegistriesToday(insulin, 1).
numberOfRegistriesToday(exercise, 1).
numberOfRegistriesToday(glucose, 1).
numberOfRegistriesToday(bloodPressure, 1).
numberOfRegistriesToday(carboHydrate, 1).
numberOfRegistriesToday(cholesterol, 1).
numberOfRegistriesToday(weight, 1).

numberOfRegistries(insulin, 5).
numberOfRegistries(exercise, 1).
numberOfRegistries(glucose, 4).
numberOfRegistries(bloodPressure, 3).
numberOfRegistries(carboHydrate, 2).
numberOfRegistries(cholesterol, 1).
numberOfRegistries(weight, 1).

numberOfTotalRegistries(300).

hasLastValueAboveMax(_).
hasLastValueUnderMin(_).

hasRecentValueAboveMax(_).
hasRecentValueUnderMin(_).

hasRecently(meal).
hadRecently(exercise).
hadRecently(insulin).

hasRecentValueHigh(_).
hasRecentValueLow(_).

hasRecentValueAbove(_,_).
hasRecentValueUnder(_, _).

hasLastValueAbove(_, _).
hasLastValueUnder(_, _).

hadRecently(_).

timeConverter(Tempo, m,TempoConvertido):- TempoConvertido is Tempo * 60.
timeConverter(Tempo, h, TempoConvertido):- TempoConvertido is Tempo * 60 * 60 .
timeConverter(Tempo, d, TempoConvertido):- TempoConvertido is Tempo * 24 * 60 * 60 .
timeConverter(Tempo, mes, TempoConvertido):- TempoConvertido is Tempo * 30 * 24 * 60 * 60 .

timeFromRBS2Sec([VALUE, MAGNITUDE], Result).