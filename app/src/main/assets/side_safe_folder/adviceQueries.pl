%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             DB  Facts                                                      %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% tested on swiProlog
% execute a simple query
%runSql(Query, Result) :- use_module(library(prosqlite)), sqlite_current_connection(OldConn), sqlite_disconnect(OldConn).
%runSql(Query, Result) :- use_module(library(prosqlite)), sqlite_connect('DB.sqlite', Conn), sqlite_query(Conn, Query, row(Result));sqlite_disconnect(Conn).

% execure a query with arguments
%runSql_with_args(Query, Variables, Result) :- use_module(library(prosqlite)), sqlite_current_connection(OldConn), sqlite_disconnect(OldConn).
%runSql_with_args(Query, Variables, Result) :- use_module(library(prosqlite)), sqlite_connect('DB.sqlite',Conn ), sqlite_format_query(Conn, Query-Variables, row(Result));sqlite_disconnect(Conn).


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the number of HypoGlycemias(glucose value under the)
%% in the last TimeSpan minutes
%%------------------------------------------------------------------------------------------------------------
numberOfRecentHypoglycemias(HypoglycemiaCount, HypoglycemiaValue, TimeSpan ) :- runSql_with_args('select count (*) from Reg_BloodGlucose as g where g.DateTime in (select DateTime from Reg_BloodGlucose where ( strftime("%s" , "now") - strftime("%s" , DateTime) < ~d )) and g.Value <= ~d order by count(*) desc limit 1', [ TimeSpan, HypoglycemiaValue], HypoglycemiaCount), integer(HypoglycemiaCount). 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the number of HyperGlycemias(glycemia value over the)
%% in the last TimeSpan minutes
%%------------------------------------------------------------------------------------------------------------
numberOfRecentHyperglycemias(HyperglycemiaCount, HyperglycemiaValue, TimeSpan ) :- runSql_with_args('select count (*) from Reg_BloodGlucose as g where g.DateTime in (select DateTime from Reg_BloodGlucose where ( strftime("%s" , "now") - strftime("%s" , DateTime) < ~d )) and g.Value >= ~d order by count(*) desc limit 1', [ TimeSpan, HyperglycemiaValue], HyperglycemiaCount), integer(HyperglycemiaCount). 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the user's age
%%------------------------------------------------------------------------------------------------------------
userAge(Age) :- runSql('select ((strftime("%J" , "now") - strftime("%J" , BDate)) * 0.00273) from UserInfo', Age), float(Age). 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the user's Weight
%%------------------------------------------------------------------------------------------------------------
userWeight(Weight) :- lastValue(weight, Weight), float(Weight).

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the user's Height
%%------------------------------------------------------------------------------------------------------------
userHeight(Height) :- runSql('select Height from UserInfo', Height), float(Height).

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user has a certain given disease
%%------------------------------------------------------------------------------------------------------------
hasDisease(Disease, Result) :- runSql_with_args('select Disease from Reg_Disease Where Disease = "~w"', [Disease], Result). /*maybe Working ... needs more tests*/

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user is currently sick
%%------------------------------------------------------------------------------------------------------------
isSick :- runSql('select * from Reg_Disease  where  EndDate is NULL', Result), Result != 0.


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the last registered value of the given parameter
%%------------------------------------------------------------------------------------------------------------
lastValue(glucose, Value) :- runSql('select Value from Reg_BloodGlucose order by DateTime Desc limit 1', Value), float(Value). 
lastValue(insulin, Value) :- runSql('select Value from Reg_Insulin order by DateTime Desc limit 1', Value), float(Value). 
lastValue(exercise, Value) :- runSql('select Value from Reg_Reg_Exercise order by DateTime Desc limit 1', Value), float(Value). 
lastValue(bloodPressure, Value) :- runSql('select Value from Reg_BloodPressure order by DateTime Desc limit 1', Value), float(Value). 
lastValue(carboHydrate, Value) :- runSql('select Value from Reg_CarboHydrate order by DateTime Desc limit 1', Value), float(Value). 
lastValue(cholesterol, Value) :- runSql('select Value from Reg_Cholesterol order by DateTime Desc limit 1', Value), float(Value). 
lastValue(weight, Value) :- runSql('select Value from Reg_Reg_Weight order by DateTime Desc limit 1', Value), float(Value). 
lastValue(hbA1c, Value) :- runSql('select Value from Reg_Reg_A1c order by DateTime Desc limit 1', Value), float(Value). 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the time passed since the user last registered a certain parameter
%%------------------------------------------------------------------------------------------------------------
timeSinceLastRegistry(insulin, Time) :- runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_Insulin order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(exercise, Time) :- runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_Exercise order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(glucose, Time) :- runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_BloodGlucose order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(bloodPressure, Time) :- runSql('select (strftime("%s", "now") - strftime("%s" , DateTime)) from Reg_BloodPressure order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(carboHydrate, Time) :- runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_CarboHydrate order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(cholesterol, Time) :- Time = runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_Cholesterol order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(weight, Time) :- Time = runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_Weight order by DateTime Desc limit 1', Time), float(Time). 
timeSinceLastRegistry(hbA1c, Time) :- Time = runSql('select (strftime("%s" , "now") - strftime("%s" , DateTime)) from Reg_A1c order by DateTime Desc limit 1', Time), float(Time). 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the number of registries of a certain parameter made today
%%------------------------------------------------------------------------------------------------------------
numberOfRegistriesToday(insulin, Count) :- runSql('select count(*)  from Reg_Insulin where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(exercise, Count) :- runSql('select count(*)  from Reg_Exercise where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(glucose, Count) :- runSql('select count(*)  from Reg_BloodGlucose where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(bloodPressure, Count) :- runSql('select count(*)  from Reg_BloodPressure where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(carboHydrate, Count) :- runSql('select count(*)  from Reg_CarboHydrate where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(cholesterol, Count) :- runSql('select count(*)  from Reg_Cholesterol where DateTime >= date("now", "0 days")', Count), integer(Count). 
numberOfRegistriesToday(weight, Count) :- runSql('select count(*)  from Reg_Weight where DateTime >= date("now", "0 days")', Count), integer(Count).

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns the number of registries of a certain parameter
%%------------------------------------------------------------------------------------------------------------
numberOfRegistries(insulin, Count) :- runSql('select count(*)  from Reg_Insulin)', Count), integer(Count).
numberOfRegistries(exercise, Count) :- runSql('select count(*)  from Reg_Exercise)', Count), integer(Count).
numberOfRegistries(glucose, Count) :- runSql('select count(*)  from Reg_BloodGlucose)', Count), integer(Count).
numberOfRegistries(bloodPressure, Count) :- runSql('select count(*)  from Reg_BloodPressure)', Count), integer(Count).
numberOfRegistries(carboHydrate, Count) :- runSql('select count(*)  from Reg_CarboHydrate)', Count), integer(Count).
numberOfRegistries(cholesterol, Count) :- runSql('select count(*)  from Reg_Cholesterol)', Count), integer(Count).
numberOfRegistries(weight, Count) :- runSql('select count(*)  from Reg_Weight)', Count), integer(Count).


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns total number of registries made
%%------------------------------------------------------------------------------------------------------------
numberOfTotalRegistries(TotalNumberOfRegistries) :- numberOfRegistriesFromListParameters([insulin, exercise, glucose, bloodPressure, carboHydrate, cholesterol, weight], TotalNumberOfRegistries).
%%------------------------------------------------------------------------------------------------------------
%% aux Rule that returns total number of registries made from a parameter list
%%------------------------------------------------------------------------------------------------------------
numberOfRegistriesFromListParameters([Parameter], ThisCount):- numberOfRegistries(Parameter, ThisCount).
numberOfRegistriesFromListParameters([Parameter|Rest], NumberRegistries):- numberOfRegistries(Parameter, ThisCount), numberOfRegistriesFromListParameters(Rest, NumberRegistries2), NumberRegistries is NumberRegistries2 + ThisCount.


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below the max value determined
%%------------------------------------------------------------------------------------------------------------
hasLastValueAboveMax(Parameter) :- lastValue(Parameter, Value), maxValue(Parameter, MaxValue), Value >= MaxValue. 
hasLastValueUnderMin(Parameter) :- lastValue(Parameter, Value), minValue(Parameter, MinValue), Value =< MinValue.

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below the max value determined
%%------------------------------------------------------------------------------------------------------------
hasRecentValueAboveMax(Parameter) :- hadRecently(Parameter), lastValue(Parameter, Value), maxValue(Parameter, MaxValue), Value >= MaxValue.
hasRecentValueAboveMax(Parameter):- hadRecently(Parameter), lastValue(Parameter, [Value1,Value2]), maxValue(Parameter, [MaxValue1, MaxValue2]), Value1 >= MaxValue1, Value2 >= MaxValue2.

hasRecentValueUnderMin(Parameter) :- hadRecently(Parameter), lastValue(Parameter, Value), minValue(Parameter, MinValue), Value =< MinValue.
hasRecentValueUnderMin(Parameter) :- hadRecently(Parameter), lastValue(Parameter, [Value1,Value2]), maxValue(Parameter, [MaxValue1, MaxValue2]), Value1 =< MaxValue1, Value2 =< MaxValue2.

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below the max value determined
%%------------------------------------------------------------------------------------------------------------
hasRecentValueHigh(Parameter) :- hadRecently(Parameter), lastValue(Parameter, Value), highValue(Parameter, HighValue), Value >= HighValue, maxValue(Parameter, MaxValue), value < MaxValue.
hasRecentValueLow(Parameter) :- hadRecently(Parameter), lastValue(Parameter, Value), lowValue(Parameter, LowValue), Value =< LowValue, minValue(Parameter, MinValue), value > MinValue .


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below the max value determined
%%------------------------------------------------------------------------------------------------------------
hasRecentValueAbove(Parameter), ComparableValue) :- hadRecently(Parameter), lastValue(Parameter, Value), Value >= ComparableValue.
hasRecentValueUnder(Parameter, ComparableValue) :- hadRecently(Parameter), lastValue(Parameter, Value), Value =< ComparableValue.


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below a given value
%%------------------------------------------------------------------------------------------------------------
hasLastValueAbove(Parameter, Value) :- lastValue(Parameter, LastValue), Value >= LastValue. 
hasLastValueUnder(Parameter, Value):- lastValue(Parameter, LastValue), Value =< LastValue. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user made a registry of a certain parameter recently
%%------------------------------------------------------------------------------------------------------------
hadRecently(Parameter) :- timeOfLastRegistry(Parameter, Time), recentTimeDefenition(Parameter, RecentTime), timeFromRBS2Sec(RecentTime, TimeInSec), Time < TimeInSec.

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user made a certain number of registries of a certain parameter today
%%------------------------------------------------------------------------------------------------------------
hasDaily(Parameter, Count) :- numberOfRegistriesToday(Parameter, TodayCount), TodayCount =< Count. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last value the user inserted is better than the value before
%%------------------------------------------------------------------------------------------------------------
insertedBetterValue(Parameter) :- lastValue(Parameter, LastValue), goodValue(Parameter, [HighEnd,LowEnd]), LastValue =< HighEnd, LastValue >= LowEnd.
insertedBetterValue(Parameter) :- getLastTwoValues(Parameter, [Value1,Value2]), checkGoodValueProgress(Parameter, [Value1,Value2]).

checkGoodValueProgress(Parameter, [Value1,Value2]) :- goodValue(Parameter, GoodValue),  Value1 < Value2, Value1 > GoodValue.
checkGoodValueProgress(Parameter, [Value1,Value2]) :- goodValue(Parameter, GoodValue),  Value1 > Value2, Value1 < GoodValue.

getLastTwoValues(Parameter, [Value1,Value2]) :- runSql('select Value from Reg_BloodGlucose order by DateTime Desc limit 1', [Value1, Value2]), float(Value1), float(Value2).


%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user has made a certain registry in a given timegap 
%%------------------------------------------------------------------------------------------------------------
hasPeriodic(Parameter, TimeGap) :- timeOfLastRegistry(Parameter, LastRegistryTime), timeFromRBS2Sec(LastRegistryTime, TimeInSec), getCurrentTime(CurrentTime), (CurrentTime - TimeGap) =< TimeInSec.

%%------------------------------------------------------------------------------------------------------------
%% Returns the time in seconds
%%------------------------------------------------------------------------------------------------------------
getCurrentTime(CurrentTime) :- runSql('select (strftime("%s", "now"))', CurrentTime), float(CurrentTime).


 %% base : timeConverter(Tempo,Unidade,TempoEmSegundos).
timeConverter(Tempo,m,TempoConvertido):- TempoConvertido is Tempo * 60.
timeConverter(Tempo,h,TempoConvertido):- TempoConvertido is Tempo * 60 * 60.
timeConverter(Tempo,d,TempoConvertido):- TempoConvertido is Tempo * 24 * 60 * 60.
timeConverter(Tempo,mes,TempoConvertido):- TempoConvertido is Tempo 30 * 24 * 60 * 60.

timeFromRBS2Sec([VALUE, MAGNITUDE], Result):- timeConverter(VALUE, MAGNITUDE, Result).