%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             DB  Querries                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%db_open(sqlite,connectionToMyDiabetesDatabase,localhost,User,Password).

%db_sql(’SELECT * FROM phonebook’,LA).

%db_close(connectionToMyDiabetesDatabase).




%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o numero de Hipoglicemias (valor de glicose abaixo do HypoglycamiaValue) 
%% nos ultimos TimeSpan minutos
%%----------------------------------
%% Rule that returns the number of HypoGlycemias(glucose value under the)
%% in the last TimeSpan minutes
%%------------------------------------------------------------------------------------------------------------

numberOfRecentHypoglycemias(HypoglycemiaCount, HypoglycemiaValue, TimeSpan ) :- 
HypoglycemiaCount = odbc_query(diabetes , "select count (*) fromReg_BloodGlucose as g where g . DateTime 
in (select DateTime from Reg_BloodGlucose where ( strftime(’%s ’ , ’ now ’) - strftime(’%s ’ , DateTime)) <"+TimeSpan+") 
and g.Value <="+HypoglycaemiaValue+" order by count(*) desc limit 1 ").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o numero de Hiperglicemias (valor de glicose acima do HyperglycamiaValue)
%% nos ultimos TimeSpan minutos
%%----------------------------------
%% Rule that returns the number of HyperGlycemias(glucose value over the)
%% in the last TimeSpan minutes
%%------------------------------------------------------------------------------------------------------------

numberOfRecentHyperglycemias(HyperglycemiaCount, HyperglycemiaValue, TimeSpan ) :- 
HyperglycemiaCount = odbc_query(diabetes , "select count (*) fromReg_BloodGlucose as g where g . DateTime 
in (select DateTime from Reg_BloodGlucose where ( strftime(’%s ’ , ’ now ’) - strftime(’%s ’ , DateTime)) <"+TimeSpan+") 
and g.Value >="+HyperglycaemiaValue+" order by count(*) limit 1 ").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna a idade do utilizador
%%----------------------------------
%% Rule that returns the user's age
%%------------------------------------------------------------------------------------------------------------

userAge(Age) :- Age = odbc_query(diabetes, ’select ((strftime('%J' , 'now') - strftime('%J' , BDate)) * 0.00273) from UserInfo’).

%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o tempo que passou desde o ultimo exercicio registado
%%----------------------------------
%% Rule that returns the time since the last registered exercise
%%------------------------------------------------------------------------------------------------------------

timeLastExercise(Time) :- Time = odbc_query(diabetes , "select  (strftime('%s' , 'now') - strftime('%s', DateTime)) 
from Reg_BloodGlucose where DateTime in (select DateTime from Reg_BloodGlucose order by DateTime desc limit 1)").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna se o Doente tem a Doença "Disease"
%%----------------------------------
%% Rule that returns if the user has the disease "Disease"
%%------------------------------------------------------------------------------------------------------------

hasDisease(Disease) :- odbc_query(diabetes , "select Disease from Reg_Disease Where Disease="+Disease).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o valor do ultimo de glucose registo do utilizador 
%%----------------------------------
%% Rule that returns the value of the last glucose registry
%%------------------------------------------------------------------------------------------------------------

lastGlucoseValue(Value) :- odbc_query(diabetes , "select Value from Reg_BloodGlucose order by Timestamp Desc limit 1).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de insulina administrada
%%----------------------------------
%% Rule that returns the time in seconds of the last insulin registry 
%%------------------------------------------------------------------------------------------------------------

timeOfLastInsulinRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Insulin order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de glicose
%%----------------------------------
%% Rule that returns the time in seconds of the last glucose registry 
%%------------------------------------------------------------------------------------------------------------

timeOfLastGlycemiaRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_BloodGlucose order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo da pressao arterial
%%----------------------------------
%% Rule that returns the time in seconds of the last arterial pressure registry
%%------------------------------------------------------------------------------------------------------------

timeOfLastBloodPressureRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_BloodPressure order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de hidratos de carbono
%%----------------------------------
%% Rule that returns the time in seconds of the last carboHydrate registry 
%%------------------------------------------------------------------------------------------------------------

timeOfLastCarboHydrateRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_CarboHydrate order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de colesterol
%%----------------------------------
%% Rule that returns the time in seconds of the last cholesterol registry 
%%------------------------------------------------------------------------------------------------------------

timeOfLastCholesterolRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Cholesterol order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo do peso
%%----------------------------------
%% Rule that returns the time in seconds of the last weight registry 
%%------------------------------------------------------------------------------------------------------------

timeOfLastWeightRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Weight order by DateTime Desc limit 1’).

%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna quantas vezes o utilizador fez exercicio
%%----------------------------------
%% Rule that returns how many times the user exercised
%%------------------------------------------------------------------------------------------------------------

timeOfLastWeightRegistry(Time) :- Time = ver kantas horas passaram e durante essas horas kantas vezes foi feito exercicio..
%odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Weight order by DateTime Desc limit 1’).


numberExercisesToday




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Test Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

hasHighGlucose :- lastGlucoseValue(Value), maxValue(glucose, MaxValue), Value >= MaxValue.
hasLowGlucose :- lastGlucoseValue(Value), minValue(glucose, MinValue), Value <= MinValue.

hasHighArterialPressure :- lastArterialPressureValue(Value), maxValue(arterialPressure, MaxValue), Value >= MaxValue.
hasLowArterialPressure :- lastArterialPressureValue(Value), minValue(arterialPressure, MinValue), Value <= MinValue.

hasHighCholesterol :- lastCholesterolValue(Value), maxValue(cholesterol, MaxValue), Value >= MaxValue.
hasLowCholesterol :- lastCholesterolValue(Value), minValue(cholesterol, MinValue), Value <= MinValue.

hasHighHbA1c :- lastHbA1cValue(Value), maxValue(hbA1c, MaxValue), Value >= MaxValue.
hasLowHbA1c :- lastHbA1cValue(Value), minValue(hbA1c, MinValue), Value <= MinValue.

hasHighWeight :- lastWeightValue(Value), maxValue(weight, MaxValue), Value >= MaxValue.
hasLowWeight:- lastWeightValue(Value), minValue(weight, MinValue), Value <= MinValue.

hasHighInsulin :- lastInsulinValue(Value), maxValue(insulin, MaxValue), Value >= MaxValue.


mealExercisedRecently :- timeLastExercise(Tempo), recentTimeDefenition(exercise, RecentTime), tempo < RecentTime.