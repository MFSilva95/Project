%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                              System Rules                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%------------------------------------------------------------------------------------------------------------
%% Reconsults get info from other needed files
%%------------------------------------------------------------------------------------------------------------



setLanguageFile(LangDescriptor, LangFile) :- atom_concat('messageFiles/advice_msg_',LangDescriptor, LangFile).



%%------------------------------------------------------------------------------------------------------------
%% Master Rule - called from the android application with the type of test "Type" requested by the user 
%%------------------------------------------------------------------------------------------------------------

masterRule( advice, Condition, Type, Language, MostUrgentAdvice):- setLanguageFile(Language, LangFile),
reconsult(['adviceQuerries','medicalRules', LangFile]),
adviceRelatedParameters(Type, SubParameterList),
getAllAdvices( Condition, SubParameterList, Type, ListAllAdvices),
filter( ListAllAdvices, [ID, _]),
msg( ID, MostUrgentAdvice).

masterRule(task, Language,TaskList):- setLanguageFile(Language), 
findall(Description ,task(Description), TaskList).


%%------------------------------------------------------------------------------------------------------------
%% For the type "Type", and list of subtypes returns a list of Advices Id and their respective Risks
%%------------------------------------------------------------------------------------------------------------

getAllAdvices( Condition, [Parameter], RegType, [[ID,Risk]] ) :- inRisk( Condition, RegType, Parameter, Risk, ID).
getAllAdvices( Condition, [Parameter|Rest], RegType,[[ID,Risk]|AdviceList]):- inRisk( Condition, RegType, Parameter, Risk, ID), getAllAdvices( Condition, Rest, RegType, AdviceList).

%%------------------------------------------------------------------------------------------------------------
%% Returns a list of Task's for the user to do
%%------------------------------------------------------------------------------------------------------------

getAllTasks([Description] ) :- hasTask(Description).
getAllTasks([Description|TaskList]):- hasTask(Description), getAllTasks(TaskList).


%%------------------------------------------------------------------------------------------------------------
%% Filter filters the list of advices and returns the advice with higher risk
%%------------------------------------------------------------------------------------------------------------

filter([Advice],Advice).
filter([[ID,Risk]|Rest],[ID,Risk]) :- filter(Rest,[_,RiskY]), Risk >= RiskY.
filter([[_,Risk]|Rest],[IDN,RiskN]) :- filter(Rest,[IDN,RiskN]), RiskN > Risk.


%%------------------------------------------------------------------------------------------------------------
%% Auxiliary function to print the list of advices
%%------------------------------------------------------------------------------------------------------------
 
printLista([]).
printLista([HEAD|REST]):- write(HEAD),nl,printLista(REST).