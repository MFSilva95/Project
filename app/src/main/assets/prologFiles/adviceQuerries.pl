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
%%------------------------------------------------------------------------------------------------------------

numberOfRecentHyporglycaemias(HypoglycaemiaCount, HypoglycaemiaValue, TimeSpan ) :- 
HypoglycaemiaCount = odbc_query(diabetes , "select count (*) fromReg_BloodGlucose as g where g . DateTime 
in (select DateTime from Reg_BloodGlucose where ( strftime(’%s ’ , ’ now ’) - strftime(’%s ’ , DateTime)) <"+TimeSpan+") 
and g.Value <="+HypoglycaemiaValue+" order by count(*) desc limit 1 ").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o numero de Hiperglicemias (valor de glicose acima do HyperglycamiaValue)
%% nos ultimos TimeSpan minutos
%%------------------------------------------------------------------------------------------------------------

numberOfRecentHyperglycaemias(HyperglycaemiaCount, HyperglycaemiaValue, TimeSpan ) :- 
HyperglycaemiaCount = odbc_query(diabetes , "select count (*) fromReg_BloodGlucose as g where g . DateTime 
in (select DateTime from Reg_BloodGlucose where ( strftime(’%s ’ , ’ now ’) - strftime(’%s ’ , DateTime)) <"+TimeSpan+") 
and g.Value >="+HyperglycaemiaValue+" order by count(*) limit 1 ").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna a idade do utilizador
%%------------------------------------------------------------------------------------------------------------

idadeUtilizador(Idade) :- Idade = odbc_query(diabetes, ’select ((strftime('%J' , 'now') - strftime('%J' , BDate)) * 0.00273) from UserInfo’).

%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna o tempo que passou desde o ultimo exercicio registado
%%------------------------------------------------------------------------------------------------------------

tempoUltimoExercicio(Tempo) :- Tempo = odbc_query(diabetes , "select  (strftime('%s' , 'now') - strftime('%s', DateTime)) 
from Reg_BloodGlucose where DateTime in (select DateTime from Reg_BloodGlucose order by DateTime desc limit 1)").


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna se o Doente tem a Doença "Disease"
%%------------------------------------------------------------------------------------------------------------

hasDisease(Disease) :- odbc_query(diabetes , "select Disease from Reg_Disease Where Disease="+Disease).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna se o Doente tem a glicemia acima de um dado valor"
%%------------------------------------------------------------------------------------------------------------

lastGlucoseValue(Value) :- odbc_query(diabetes , "select Value from Reg_BloodGlucose order by Value Desc limit 1).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de insulina administrada
%%------------------------------------------------------------------------------------------------------------

timeOfLastInsulinRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Insulin order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de glycemia
%%------------------------------------------------------------------------------------------------------------

timeOfLastGlycemiaRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_BloodGlucose order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo da pressao arterial
%%------------------------------------------------------------------------------------------------------------

timeOfLastBloodPressureRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_BloodPressure order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de hidratos de carbono
%%------------------------------------------------------------------------------------------------------------

timeOfLastCarboHydrateRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_CarboHydrate order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo de colesterol
%%------------------------------------------------------------------------------------------------------------

timeOfLastCholesterolRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Cholesterol order by DateTime Desc limit 1’).


%%------------------------------------------------------------------------------------------------------------
%% Regra que retorna em seguntos o tempo do ultimo registo do peso
%%------------------------------------------------------------------------------------------------------------

timeOfLastWeightRegistry(Time) :- Time = odbc_query(diabetes, ’select (strftime('%s' , 'now') - strftime('%s' , DateTime)) from Reg_Weight order by DateTime Desc limit 1’).
