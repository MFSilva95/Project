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

maxTimeToBeRecent(glucose, 86400). 

maxValue(glucose, 120).
maxValue(insulin, 120).
maxValue(weight, 120).
maxValue(hbA1c, 120).
maxValue(cholesterol, 120).
maxValue(arterialPressure, 120).

minValue(glucose, 70).
minValue(insulin, 0).
minValue(weight, 50).
minValue(hbA1c, 50).
minValue(cholesterol, 50).
minValue(arterialPressure, 50).

recentTimeDefenition(exercise, 3600).
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

adviceRelatedParameters(glicose, [glicose,insulin,hbA1c]).
adviceRelatedParameters(exercise, [food,insulin,exercise]).

%In case no subdivision is defined

adviceRelatedParameters(Any, [Any]). % to test


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

inRisk( end, glucose, _, 10, ID) :- ID = 'hasHighGlucose', hasHighGlucose.
inRisk( end, glucose, _, 10, ID) :- ID = 'hasLowGlucose', hasLowGlucose.

inRisk( end, hbA1c, _, 10, ID) :- ID = 'hasHighHbA1c', hasHighHbA1c.
inRisk( end, hbA1c, _, 10, ID) :- ID = 'hasLowHbA1c', hasLowHbA1c.

inRisk( end, cholesterol, _, 10, ID) :- ID = 'hasHighCholesterol', hasHighCholesterol.
inRisk( end, cholesterol, _, 10, ID) :- ID = 'hasLowCholesterol', hasLowCholesterol.

inRisk( end, insulin, _, 10, ID) :- ID = 'hasHighInsulin', hasHighInsulin.

inRisk( end, weight, _, 3, ID) :- ID = 'hasHighWeight', NOT bloqued(weight), hasHighWeight.
inRisk( end, weight, _, 3, ID) :- ID = 'hasLowWeight', NOT bloqued(weight), hasLowWeight.

inRisk( end, arterialPressure, _, 3, ID) :- ID = 'hasHighArterialPressure', hasHighArterialPressure.
inRisk( end, arterialPressure, _, 3, ID) :- ID = 'hasLowArterialPressure', hasLowArterialPressure.

inRisk( start, meal, meal, 3, ID) :- ID = 'mealExercisedRecently', hasExercisedRecently.
inRisk( start, meal, meal, 3, ID) :- ID = 'mealUserWithHighWeight', hasHighWeight.

%% Null case: No advice to be given
inRisk( _, _, _, 0, ID) :- ID = 'NULL'.


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

hasTask(Description) :- numberExercisesToday < 2, msg('Tsk_doExerciseTwoTimesADay', Description).
