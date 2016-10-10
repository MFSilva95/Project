%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                              System Rules                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%------------------------------------------------------------------------------------------------------------
%% Reconsults get info from other needed files
%%------------------------------------------------------------------------------------------------------------



%%setLanguageFile(LangDescriptor, LangFile) :- atom_concat('prologFiles/messageFiles/advice_msg_',LangDescriptor, LangFile).

%%profileFileLocation('testFiles/test.pl').

%%writeProfile(ProfileFact):- profileFileLocation(Location), tell(Location), atom_concat(ProfileFact,'.', NewProfileFact), write(NewProfileFact), nl, listing(ProfileFact), told.

getProfile(4).
%%getProfile(Result):- profileFileLocation(Location), reconsult(Location), profile(Result).
%%getProfile(Result):- profileFileLocation(Location), userProfileDefinition, reconsult(Location), profile(Result).

%%userProfileDefinition:- numberOfTotalRegistries(TotalNumberOfRegistries), TotalNumberOfRegistries =< 100, writeProfile('profile(1)').
%%userProfileDefinition:- numberOfTotalRegistries(TotalNumberOfRegistries), TotalNumberOfRegistries > 100, TotalNumberOfRegistries =< 500, writeProfile('profile(2)').
%%userProfileDefinition:- numberOfTotalRegistries(TotalNumberOfRegistries), TotalNumberOfRegistries > 500, TotalNumberOfRegistries =< 1000, writeProfile('profile(3)').
%%userProfileDefinition:- numberOfTotalRegistries(TotalNumberOfRegistries), TotalNumberOfRegistries > 1000, TotalNumberOfRegistries =< 5000, writeProfile('profile(4)').


%%------------------------------------------------------------------------------------------------------------
%% Master Rule - called from the android application with the type of test "Type" requested by the user
%%------------------------------------------------------------------------------------------------------------

%%Retorna apenas o advice mais urgente do tipo de registo type

masterRule( single_advice, Condition, Type, MostUrgentAdvice):- 
getAllAdvices( Condition, Type, ListAllAdvices),
filter( ListAllAdvices, [ID, _]),
msg( ID, MostUrgentAdvice).

%%Retorna o numero de conselhos indicado pelo user Profile

masterRule( multiple_advice, Condition, Type, ListFilteredAdvices):-
getAllAdvices( Condition, Type, ListAllAdvices),
quick_sort(ListAllAdvices, OrderedAdviceIDList),
getProfile(ProfileNumber),
length(OrderedAdviceIDList, ListLength),
minNumAdv(ProfileNumber, ListLength, MinNumber),
filterByProfile( ListFilteredIds, OrderedAdviceIDList, MinNumber),
getListFilteredAdvices( ListFilteredIds, ListFilteredAdvices ).

masterRule(task, TaskList):- findall(ID , hasTask(exercise, ID), TaskIDList), getAllTasks(TaskIDList, TaskList).

getAllTasks([IDListHead], [[SmallTxt, ExpandedTxt, Alarm, Urgency]]):- msg(IDListHead, SmallTxt, ExpandedTxt, Alarm, Urgency).
getAllTasks([IDListHead|Rest], [[SmallTxt, ExpandedTxt, Alarm, Urgency]|TaskList]):- msg(IDListHead, SmallTxt, ExpandedTxt, Alarm, Urgency), getAllTasks(Rest, TaskList).


%%------------------------------------------------------------------------------------------------------------
%% Filter filters the list of advices and returns the advice with higher risk
%%------------------------------------------------------------------------------------------------------------

filter([Advice],Advice).
filter([[ID,Risk]|Rest],[ID,Risk]) :- filter(Rest,[_,RiskY]), Risk >= RiskY.
filter([[_,Risk]|Rest],[IDN,RiskN]) :- filter(Rest,[IDN,RiskN]), RiskN > Risk.

%%------------------------------------------------------------------------------------------------------------
%% Filter filters the list of advices and returns the number of advices the users profile dictates.
%%------------------------------------------------------------------------------------------------------------

filterByProfile([],_,0).
filterByProfile([H|ListFiltered],[H|L], ProfileNumber) :- filterByProfile(ListFiltered,L, K1), ProfileNumber is K1 + 1.


%%------------------------------------------------------------------------------------------------------------
%% Searches a list of possible causes for a crisis
%%------------------------------------------------------------------------------------------------------------

searchCause(CrisisType, ListCauses):- findall(Cause, possibleCause(CrisisType, Cause), ListCauses).

%%------------------------------------------------------------------------------------------------------------
%% Takes the filtered list of IDs and returns a list of Advices
%%------------------------------------------------------------------------------------------------------------

getListFilteredAdvices([[ID|_]], [MostUrgentAdvice]) :- msg( ID, MostUrgentAdvice).
getListFilteredAdvices([[ID|_]|Rest], [MostUrgentAdvice|ListFilteredAdvices]) :- msg( ID, MostUrgentAdvice), getListFilteredAdvices( Rest, ListFilteredAdvices).

%%------------------------------------------------------------------------------------------------------------
%% For the type "Type", and list of subtypes returns a list of Advices Id and their respective Risks
%%------------------------------------------------------------------------------------------------------------

getAdvices( Condition, [Parameter], [[ID,Risk]] ) :- inRisk( Condition, Parameter, Risk, ID).
getAdvices( Condition, [Parameter|Rest], [[ID,Risk]] ) :- inRisk( Condition, Parameter, Risk, ID), getAdvices( Condition, Rest, [[ID,Risk]] ).


%%------------------------------------------------------------------------------------------------------------
%% For the type "Type", and list of subtypes returns a list of Advices Id and their respective Risks
%%------------------------------------------------------------------------------------------------------------

getAllAdvices( Condition, Parameter, TaskList ) :- findall([ID,Risk] , inRisk( Condition, Parameter, Risk, ID), TaskList).

%%------------------------------------------------------------------------------------------------------------
%% Returns a list of Task's for the user to do
%%------------------------------------------------------------------------------------------------------------

%getAllTasks([Description] ) :- hasTask(Description).
%getAllTasks([Description|TaskList]):- hasTask(Description), getAllTasks(TaskList).

%% verify if proffile number is bigger than the number of advices
%%returns the minimum

minNumAdv(Num1,Num2,Num1):- Num1<Num2.
minNumAdv(Num1,Num2,Num2).


%%------------------------------------------------------------------------------------------------------------
%% Auxiliary function to print the list of advices
%%------------------------------------------------------------------------------------------------------------

printLista([]).
printLista([HEAD|REST]):- write(HEAD), nl, printLista(REST).

element_at(X,[X|_],1).
element_at(X,[_|L],K) :- element_at(X,L,K1), K is K1 + 1.


%%---------------------------------------------------

quick_sort(List,Sorted):-q_sort(List,[],Sorted).
q_sort([],Acc,Acc).
q_sort([H|T],Acc,Sorted):-
    pivoting(H,T,L1,L2),
    q_sort(L1,Acc,Sorted1),q_sort(L2,[H|Sorted1],Sorted).


pivoting([_|Urg],[],[],[]).
pivoting([ID|Urg],[[IDX|X]|T],[[IDX|X]|L],G):-X=<Urg,pivoting([ID|Urg],T,L,G).
pivoting([ID|Urg],[[IDX|X]|T],L,[[IDX|X]|G]):-X>Urg,pivoting([ID|Urg],T,L,G).

%%---------------------------------------------------

verifyOutDatedParameters(Parameter, [MissingID]) :- not(hasRecently(Parameter)), atom_concat('missing:', Parameter, MissingID).
verifyOutDatedParameters([Parameter|Rest], MissingIDList) :- not(hasRecently(Parameter)), atom_concat('missing:', Parameter, MissingID), verifyOutDatedParameters(Rest, MissingIDList).


checkAllCauses(Situation, Explanation):-
possibleCause(Situation, PossibleCauses),
checkPossibleCauses(PossibleCauses, ConfirmedCauses),
msg(Situation, ConfirmedCauses, Explanation).

checkPossibleCauses([(RegType, PresenceOf)], [(RegType, PresenceOf)]):- isCausePresent(RegType, PresenceOf).
checkPossibleCauses([(RegType, PresenceOf)|Rest], [(RegType, PresenceOf)|ListConfirmedCauses]):-
isCausePresent(RegType, PresenceOf),
checkPossibleCauses(Rest, ListConfirmedCauses).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%  de fora para ja
%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%masterRule( single_advice, Condition, Type, Language, ListFilteredAdvices):- setLanguageFile(Language, LangFile),
%% reconsult(['prologFiles/adviceQuerries','prologFiles/medicalRules', LangFile]),
%% adviceRelatedParameters(Type, ParameterList),
%% getAdvices( Condition, ParameterList, ListAllAdvices),
%% filter( ListAllAdvices, [ID, _]),
%% msg( ID, MostUrgentAdvice).


%%masterRule( multiple_advice, Condition, Type, Language, UserType, ListFilteredAdvices):- setLanguageFile(Language, LangFile),
%% reconsult(['prologFiles/adviceQuerries','prologFiles/medicalRules', LangFile]),
%% adviceRelatedParameters(Type, ParameterList),
%% verifyOutDatedParameters,
%% getAllAdvices( Condition, ParameterList, ListAllAdvices),
%% quick_sort(ListAllAdvices, OrderedAdviceIDList),
%% profile(UserType, ProfileNumber),
%% filterByProfile( OrderedAdviceIDList, ListFilteredIds, ProfileNumber),
%% getListFilteredAdvices( ListFilteredIds, ListFilteredAdvices ).