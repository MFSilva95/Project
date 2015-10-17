%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             DB  Facts                                                      %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% tested on swiProlog
% execute a simple query
runSql(Query, Result) :- use_module(library(prosqlite)), sqlite_current_connection(OldConn), sqlite_disconnect(OldConn).
runSql(Query, Result) :- use_module(library(prosqlite)), sqlite_connect('DB.sqlite', Conn), sqlite_query(Conn, Query, row(Result));sqlite_disconnect(Conn).

% execure a query with arguments
runSql_with_args(Query, Variables, Result) :- use_module(library(prosqlite)), sqlite_current_connection(OldConn), sqlite_disconnect(OldConn).
runSql_with_args(Query, Variables, Result) :- use_module(library(prosqlite)), sqlite_connect('DB.sqlite',Conn ), sqlite_format_query(Conn, Query-Variables, row(Result));sqlite_disconnect(Conn).



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
%% Rule that returns if the last registry of a certain parameter was above or below the max value determined
%%------------------------------------------------------------------------------------------------------------
hasLastValueAboveMax(Parameter) :- lastValue(Parameter, Value), maxValue(Parameter, MaxValue), Value >= MaxValue. 
hasLastValueUnderMin(Parameter) :- lastValue(Parameter, Value), minValue(Parameter, MinValue), Value =< MinValue. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the last registry of a certain parameter was above or below a given value
%%------------------------------------------------------------------------------------------------------------
hasLastValueAbove(Parameter, Value) :- lastValue(Parameter, LastValue), Value >= LastValue. 
hasLastValueUnder(Parameter, Value):- lastValue(Parameter, LastValue), Value =< LastValue. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user made a registry of a certain parameter recently
%%------------------------------------------------------------------------------------------------------------
hadRecently(Parameter) :- timeOfLastRegistry(Parameter, Time), recentTimeDefenition(Parameter, RecentTime), Time < RecentTime. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user made a certain number of registries of a certain parameter today
%%------------------------------------------------------------------------------------------------------------
hasDaily(Parameter, Count) :- numberOfRegistriesToday(Parameter, TodayCount), TodayCount =< Count. 

%%------------------------------------------------------------------------------------------------------------
%% Rule that returns if the user has made a certain registry in a given timegap 
%%------------------------------------------------------------------------------------------------------------
hasPeriodic(Parameter, TimeGap) :- timeOfLastRegistry(Parameter, LastRegistryTime), getCurrentTime(CurrentTime), (CurrentTime - TimeGap) =< LastRegistryTime. 

%%------------------------------------------------------------------------------------------------------------
%% Returns the time in seconds
%%------------------------------------------------------------------------------------------------------------
getCurrentTime(CurrentTime) :- runSql('select (strftime("%s", "now"))', CurrentTime), float(CurrentTime).  