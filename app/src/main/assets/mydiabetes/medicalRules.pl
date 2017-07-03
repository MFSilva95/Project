%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                            Global Values                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Define global values
%% Time is always defined in seconds
%%------------------------------------------------------------------------------------------------------------

maxTimeUntested(glucose, [3,h]). %3h
maxTimeUntested(carboHydrate, [3,h]). %3h
maxTimeUntested(insulin, [3,h]). %3h
maxTimeUntested(carbs, [24,h]).
maxTimeUntested(arterialPressure, [24,h]). %24h
maxTimeUntested(cholesterol, [1,mes]). %1mes
maxTimeUntested(weight, 2592000). %1 mes
maxTimeUntested(hbA1c, 86400). %24h

recentTimeDefenition(glucose, [3,h]). %3h
recentTimeDefenition(carboHydrate, [3,h]). %3h
recentTimeDefenition(insulin, [3,h]). %3h
recentTimeDefenition(carbs, [24,h]).
recentTimeDefenition(arterialPressure, 86400). %24h
recentTimeDefenition(cholesterol, 2592000). %1mes
recentTimeDefenition(weight, 2592000). %1 mes
recentTimeDefenition(hbA1c, 86400). %24h

maxValue(glucose, 80).
maxValue(insulin, 120).
maxValue(weight, MaxWeight) :- userHeight(Height), MaxWeight is (Height * Height* 25).
maxValue(hbA1c, 120).
maxValue(carbs, 200).
maxValue(cholesterol, 120).
maxValue(arterialPressure, 120).

hasHigh(Parameter) :- lastValue(Parameter, Value), maxValue(Parameter,MaxValue), Value >= MaxValue.

hasLow(Parameter) :- lastValue(Parameter, Value), minValue(Parameter,MinValue), Value =< MinValue. 

minValue(glucose, (70.0)).
minValue(weight, MinWeight) :- userHeight(Height), MinWeight is (Height * Height* 18.5).
minValue(hbA1c, 50.0).
minValue(cholesterol, 50.0).
minValue(arterialPressure, 50.0).

safetyInterval(600).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Risk Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% To define a test of risk a fact must be inserted
%% this fact must be of form inRisk( Situation, Type, SubType, Risk, ID) :- 
%% NOT bloqued(glucose),
%% (conditions to activate separeted by ','), msg( start, pt, ID, _). 
%% The facts must be inserted in decrescent order of Risk
%% The facts must be inserted from the most specific to the more abstracts
%%------------------------------------------------------------------------------------------------------------


%%type glicemia ----------
inRisk( end, glucose, 9, hasHypoGlycemia) :- hasRecentValueUnderMin(glucose). %%acao imediata
%%inRisk( end, glucose, 9, hasHyperGlycemia) :-hasRecentValueAboveMax(glucose). %%acao imediata -> ver que aconselhar

%%Glicemia em estado anomalo ver causa!!

inRisk(end, glucose, 5, MsgID):- hasRecentValueLow(glucose),
getPossibleCauses(lowGlucose, PossibleCauses),
getMessageID(hasLowGlucose, PossibleCauses, MsgID).

inRisk(end, glucose, 5, MsgID):- hasRecentValueHigh(glucose),
getPossibleCauses(highGlucose, PossibleCauses),
getMessageID(highGlucose, PossibleCauses, MsgID).


%%type insulin ----------
%%inRisk( start, insulin, 9, hadHypoBeforeInsulin :- hasRecentValueUnderMin(glucose).
%%inRisk( start, insulin, 9, hadLowBeforeInsulin) :- hasRecentValueLow(glucose).
%%inRisk( start, insulin, 9, hadRecentInsulinBeforeInsulin) :- hasRecently(insulin).
%%inRisk( start, insulin, 9, hadRecenlyExerciseBeforeInsulin) :-hasRecently(exercise).

%%type carboHydrate ----------
%%inRisk( start, carboHydrate, 8, hadHypoGlycemia) :- hasRecentValueUnderMin(glucose).
%%inRisk( start, carboHydrate, 9, hadHyperGlycemia) :- hasRecentValueAboveMax(glucose).
%%inRisk( start, carboHydrate, 9, hadHighBeforeMeal) :- hasRecentValueHigh(glucose).
%%inRisk( start, carboHydrate, 9, hadLowGlucose) :- hasRecentValueLow(glucose).
%%inRisk( start, carboHydrate, 3, mealExercisedRecently) :- hasRecently(exercise).

%%inRisk( end, carboHydrate, 9, hasHyperGlycemia) :- hasRecentValueAboveMax(glucose).
%%inRisk( end, carboHydrate, 9, hasHypoGlycemia) :- hasRecentValueUnderMin(glucose).
%%inRisk( end, carboHydrate, 9, hasHighGlucose) :- hasRecentValueHigh(glucose).
%%inRisk( end, carboHydrate, 9, hasLowGlucose) :- hasRecentValueLow(glucose).

%%type exercise ----------
%%inRisk( start, exercise, 9, hasLowGlucose) :- hasRecentValueLow(glucose).
%%inRisk( start, exercise, 9, hadRecentInsulin) :- hasRecently(insulin).
%%inRisk( start, exercise, 9, hadRecentExercise) :- hasRecently(exercise).
%%inRisk( start, exercises, 9, haventEatBeforeExercise) :- not(hasRecently(carboHydrate)).

%%type disease ----------
%%inRisk( end, disease, 9, isSick) :- isSick, createTask("check your glycaemia for cetones").

%%type weight ----------
%%inRisk( end, weight, 9, gotBetterWeight) :- insertedBetterValue(weight).

%%type hbA1c ----------
%%inRisk( start, hbA1c, 9, gotBetterHbA1c) :- insertedBetterValue(hbA1c).
%%inRisk( end, hbA1c, 9, hasHighHbA1c) :- hasRecentValueAboveMax(hbA1c).

%%arterialPressure ----------
%%inRisk( end, arterialPressure, 3, hasHighArterialPressure) :- hasRecentValueAboveMax(arterialPressure).
%%inRisk( end, arterialPressure, 3, hasLowArterialPressure) :- hasRecentValueUnderMin(arterialPressure).

%%inRisk( start, true, carboHydrate, 3, mealUserWithHighWeight) :- hasHighWeight.

%%hasHipoglicemia(lightModerate) :- lastValue(glucose, Value), minValue(glucose, GlucoseMinValue), Value =< GlucoseMinValue, Value > (50.0).



testeMedico(Resultado):- atom_concat('lol','lel', Resultado).


%%-------------------------------------------------------------------------------------------------------------REVER-------------------


%% tendo em conta que o valor de colesterol � obtido a partir de analises clinicas, parece redundante criar alarme a partir de um valor que o
%% utilizador ja sabe que � alto. Parece m muito mais relevante fazer um levantamento das medi�oes e fazer um balan�o. ex: felicitar por este valor estar
%% abaixo da m�dia de regitos.

%%inRisk( end, cholesterol, 9, ID) :- ID = 'hasHighCholesterol', hasRecentValueAboveMax(cholesterol).                                                                                                                                                                                                     , ID) :- ID = 'hasHighInsulin', hasHigh(insulin).

%%A pessoa com peso a mais, tem conhecimento deste facto. Estar a chamar a aten�ao para o peso pode ser prejudicial.
%% uma ideia seria analisar os valores anteriores e felicitar se a pessoa se aproximou do peso correto.

%%inRisk( end, true, weight, 3, ID) :- ID = 'hasHighWeight', not(bloqued(weight)), hasHigh(weight).

%% pessoas demasiado magras podem sofrer de um problema ainda mais grave...
%% neste caso � mau dizer k a pessoa ta mt magra, mas inda � pior dizer k se ganhou peso, ao se aproximar do peso correto (casos de anorexia..)

%%inRisk( end, true, weight, 3, ID) :- ID = 'hasLowWeight', not(bloqued(weight)), hasLow(weight).


%%-------------------------------------------------------------------------------------------------------------REVER-------------------



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Task Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Essencial Tasks: the SABR needs these registry types updated to function properlly 
%%------------------------------------------------------------------------------------------------------------

%%hasTask(RegistryType, Description) :-
%%timeSinceLastRegistry(RegistryType, [LastRegTimeValue|LastRegTimeMagnitude]),
%%timeConverter(LastRegTimeValue, LastRegTimeMagnitude, LastRegTimeInSec),
%%maxTimeUntested(RegistryType, [MaxTimeValue|MaxTimeMagnitude]),
%%timeConverter(MaxTimeValue, MaxTimeMagnitude, maxTimeUntestedInSec),
%%safetyInterval(SafetyInterval),
%%LastRegTimeInSec > maxTimeUntestedInSec - SafetyInterval,
%%atom_concat('Tsk_Reg_', glucose, MessageId),
%%msg(MessageId, Description).


%%------------------------------------------------------------------------------------------------------------
%% Other Tasks: these tasks are advised to the majority of diabetics 
%%------------------------------------------------------------------------------------------------------------

hasTask(RegistryType, ID) :-
numberOfRegistriesToday(RegistryType, NumRegistries),
NumRegistries < 2,
atom_concat('Tsk_doTwoTimesADay_', RegistryType, ID).


%%------------------------------------------------------------------------------------------------------------
%% Tasks For Testing only START
%%------------------------------------------------------------------------------------------------------------

hasTask(RegistryType, ID) :-
numberOfRegistriesToday(RegistryType, NumRegistries),
NumRegistries < 3,
atom_concat('Tsk_doOnceAMonth_', RegistryType, ID).

%%------------------------------------------------------------------------------------------------------------
%% Tasks For Testing only END
%%------------------------------------------------------------------------------------------------------------

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Extra Rules                                                    %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                        Casuality Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

possibleCrisisExplanation(lowGlucose, [[exercise, 1],[meal, 0],[insulin, 1]]).
possibleCrisisExplanation(highGlucose, [[meal, 1],[insulin, 0],[disease, 1]]).
%%possibleCause(hypoglycemia, stress).