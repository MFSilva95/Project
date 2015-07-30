%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                              System Rules                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%------------------------------------------------------------------------------------------------------------
%% Reconsults get info from other needed files
%%------------------------------------------------------------------------------------------------------------

reconsult("adviceQuerries").
reconsult("riskRules").

setLanguageFile(LangDescriptor) :- atom_concat('advice_msg_',LangDescriptor, LangFile), reconsult(LangFile).



%%------------------------------------------------------------------------------------------------------------
%% Master Rule - called from the android application with the type of test "Type" requested by the user 
%%------------------------------------------------------------------------------------------------------------

masterRule(advice, Type, Language,MostUrgentAdvice):- setLanguageFile(Language), adviceTypes(Type, SubTypeList), 
getAllAdvices(Type, SubTypeList, ListAllAdvices), 
%printLista(ListAllAdvices), 
filter(ListAllAdvices, MostUrgentAdvice).
%write(MostUrgentAdvice), nl.


masterRule(task, Language,TaskList):- setLanguageFile(Language), 
findall(Description ,task(Description), TaskList).


%%------------------------------------------------------------------------------------------------------------
%% For the type "Type", and list of subtypes returns a list of Advices Id and their respective Risks
%%------------------------------------------------------------------------------------------------------------

getAllAdvices(Type ,[SubType], [[ID,Risk]] ) :- inRisk(Type,SubType,Risk,ID).
getAllAdvices(Type, [SubType|Rest],[[ID,Risk]|AdviceList]):- inRisk(Type,SubType,Risk,ID), getAllAdvices(Type, Rest,AdviceList).

%%------------------------------------------------------------------------------------------------------------
%% Returns a list of Task's for the user to do
%%------------------------------------------------------------------------------------------------------------

%getAllTasks([[Description]] ) :- hasTask(Description).
%getAllTasks([[Description]|TaskList]):- hasTask(Description), getAllTasks(TaskList).


%%------------------------------------------------------------------------------------------------------------
%% Filter as the name implies, filters the list of advices and returns the advice with higher risk
%%------------------------------------------------------------------------------------------------------------

filter([Advice],Advice).
filter([[ID,Risk]|Rest],[ID,Risk]) :- filter(Rest,[_,RiskY]), Risk >= RiskY.
filter([[_,Risk]|Rest],[IDN,RiskN]) :- filter(Rest,[IDN,RiskN]), RiskN > Risk.


%%------------------------------------------------------------------------------------------------------------
%% Auxiliary function to print the list of advices
%%------------------------------------------------------------------------------------------------------------
 
%printLista([]).
%printLista([HEAD|REST]):- write(HEAD),nl,printLista(REST).