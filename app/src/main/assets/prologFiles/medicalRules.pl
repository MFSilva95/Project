%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                            Global Values                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Define global values
%% Time is always defined in seconds
%%------------------------------------------------------------------------------------------------------------

maxTimeUntested(glucose, 10800). %3h
maxTimeUntested(meal, 10800). %3h
maxTimeUntested(insulin, 10800). %3h
maxTimeUntested(carbs, 86400).
maxTimeUntested(arterialPressure, 86400). %24h
maxTimeUntested(cholesterol, 2592000). %1mes
maxTimeUntested(weight, 2592000). %1 mes
maxTimeUntested(hbA1c, 86400). %24h

recentTimeDefenition(glucose, 10800). %3h
recentTimeDefenition(meal, 10800). %3h
recentTimeDefenition(insulin, 10800). %3h
recentTimeDefenition(carbs, 86400).
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
%                                         Parameter Correlation                                              %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% To define a type subdivision a fact must be inserted
%% this fact must be of form adviceTypes(TypeToDivide, ListOfTypeDivisions)
%%------------------------------------------------------------------------------------------------------------

adviceRelatedParameters(glucose, [insulin,hbA1c]).
adviceRelatedParameters(exercise, [food,insulin]).

%In case no subdivision is defined

adviceRelatedParameters(_, [_]). % to test


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

inRisk( end, glucose, _, 10, ID) :- ID = 'hasHighGlucose', hasHigh(glucose).
inRisk( end, glucose, _, 10, ID) :- ID = 'hasLowGlucose', hasLow(glucose).

inRisk( end, hbA1c, _, 10, ID) :- ID = 'hasHighHbA1c', hasHigh(hbA1c).
inRisk( end, hbA1c, _, 10, ID) :- ID = 'hasLowHbA1c', hasLow(hbA1c).

inRisk( end, cholesterol, _, 10, ID) :- ID = 'hasHighCholesterol', hasHigh(cholesterol).
inRisk( end, cholesterol, _, 10, ID) :- ID = 'hasLowCholesterol', hasLow(cholesterol).

inRisk( end, insulin, _, 10, ID) :- ID = 'hasHighInsulin', hasHigh(insulin).

inRisk( end, weight, _, 3, ID) :- ID = 'hasHighWeight', not(bloqued(weight)), hasHigh(weight).
inRisk( end, weight, _, 3, ID) :- ID = 'hasLowWeight', not(bloqued(weight)), hasLow(weight).

inRisk( end, arterialPressure, _, 3, ID) :- ID = 'hasHighArterialPressure', hasHigh(arterialPressure).
inRisk( end, arterialPressure, _, 3, ID) :- ID = 'hasLowArterialPressure', hasLow(arterialPressure).

inRisk( start, meal, meal, 3, ID) :- ID = 'mealExercisedRecently', hasRecently(exercise).
inRisk( start, meal, meal, 3, ID) :- ID = 'mealUserWithHighWeight', hasHighWeight.

%% Null case: No advice to be given
inRisk( _, _, _, 0, ID) :- ID = 'NULL'.


hasHipoglicemia(lightModerate) :- lastValue(glucose, Value), minValue(glucose, GlucoseMinValue), Value =< GlucoseMinValue, Value > (50.0).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Task Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Essencial Tasks: the SABR needs these registry types updated to function properlly 
%%------------------------------------------------------------------------------------------------------------

hasTask(Description) :- timeOfLastGlucoseRegistry(LastRegTime), maxTimeUntested(glucose, MaxTime), safetyInterval(SafetyInterval), LastRegTime > MaxTime - SafetyInterval, msg('Tsk_glucoseReg', Description).
hasTask(Description) :- timeOfLastHbA1cRegistry(LastRegTime), maxTimeUntested(hbA1c, MaxTime), safetyInterval(SafetyInterval), LastRegTime > MaxTime - SafetyInterval, msg('Tsk_hbA1cReg', Description).
hasTask(Description) :- timeOfLastArterialPressureRegistry(LastRegTime), maxTimeUntested(arterialPressure, MaxTime), safetyInterval(SafetyInterval), LastRegTime > MaxTime - SafetyInterval, msg('Tsk_arterialPReg', Description).
hasTask(Description) :- timeOfLastWeightRegistry(LastRegTime), maxTimeUntested(weight, MaxTime), safetyInterval(SafetyInterval), LastRegTime > MaxTime - SafetyInterval, msg('Tsk_weightReg', Description).
hasTask(Description) :- timeOfLastCholesterolRegistry(LastRegTime), maxTimeUntested(cholesterol, MaxTime), safetyInterval(SafetyInterval), LastRegTime > MaxTime - SafetyInterval, msg('Tsk_cholesterolReg', Description).

%%------------------------------------------------------------------------------------------------------------
%% Other Tasks: these tasks are advised to the majority of diabetics 
%%------------------------------------------------------------------------------------------------------------

%hasTask(Description) :- numberExercisesToday < 2, msg('Tsk_doExerciseTwoTimesADay', Description).
