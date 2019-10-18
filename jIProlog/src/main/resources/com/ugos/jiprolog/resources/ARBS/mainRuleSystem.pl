%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                              System Rules                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

path2FILES('INTERNAL://com/ugos/jiprolog/resources/ARBS/langFiles/advice_msg_').



	
% can be used by the android to remove all the consulted messages in order to import messages in another language
removeLangFiles:- retractall(msg(_,_)). 

% used to set or change the messages language
setLanguage(LangDescriptor):- removeLangFiles, setLanguageFile(LangDescriptor, LangFile), consult(LangFile). 

% aux. func to get the messages path
setLanguageFile(LangDescriptor, LangFile_path) :- path2FILES(PATH), atom_concat(PATH, LangDescriptor, LangFile), atom_concat(LangFile, '.pl', LangFile_path).


%%------------------------------------------------------------------------------------------------------------
%% Master Rule - called from the android application with the type of test "Type" requested by the user
%%------------------------------------------------------------------------------------------------------------


%getMostUrgentAdvice( single_advice, Condition, RecordType, MostUrgentAdvice):-
%	getAllAdvices( Condition, RecordType, ListAllAdvices),
%	filter( ListAllAdvices, [ID, _]),
%	msg( ID, MostUrgentAdvice).
	
	
getMostUrgentAdvice( NumberAdvice, Condition, RecordType, ListFilteredAdvices):-
	getAllAdvices( Condition, RecordType, ListAllAdvices),
	quick_sortado(ListAllAdvices, OrderedAdviceIDList),
	length(OrderedAdviceIDList, ListLength),
	minNumAdv(NumberAdvice, ListLength, MinNumber),
	filterByProfile( ListFilteredIds, OrderedAdviceIDList, MinNumber),
	getListFilteredAdvices( ListFilteredIds, ListFilteredAdvices ).
	
	
%%------------------------------------------------------------------------------------------------------------
%% For the type "Type", and list of subtypes returns a list of Advices Id and their respective Risks
%%------------------------------------------------------------------------------------------------------------

getAllAdvices( Condition, Parameter, TaskList ) :- findall([ID,Risk] , inRisk( Condition, Parameter, Risk, ID), TaskList).

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
%% Takes the filtered list of IDs and returns a list of Advices
%%------------------------------------------------------------------------------------------------------------

getListFilteredAdvices([[ID|_]], [MostUrgentAdvice]) :- msg( ID, MostUrgentAdvice).
getListFilteredAdvices([[ID|_]|Rest], [MostUrgentAdvice|ListFilteredAdvices]) :- msg( ID, MostUrgentAdvice), getListFilteredAdvices( Rest, ListFilteredAdvices).

%%------------------------------------------------------------------------------------------------------------
%% Searches a list of possible causes for a crisis
%%------------------------------------------------------------------------------------------------------------


%getPossibleCauses(Crisis, CauseList):- possibleCrisisExplanation(Crisis, PossibleCauseList), searchCause(PossibleCauseList, CauseList).


%%checkAllCauses(Situation, Explanation):-
%%possibleCause(Situation, PossibleCauses),
%%checkPossibleCauses(PossibleCauses, ConfirmedCauses),
%%msg(Situation, ConfirmedCauses, Explanation).

%%checkPossibleCauses([(RegType, PresenceOf)], [(RegType, PresenceOf)]):- isCausePresent(RegType, PresenceOf).
%%checkPossibleCauses([(RegType, PresenceOf)|Rest], [(RegType, PresenceOf)|ListConfirmedCauses]):-
%%isCausePresent(RegType, PresenceOf),
%%checkPossibleCauses(Rest, ListConfirmedCauses).


%searchCause([],[nocause]).
%searchCause([[Type, Bool]], [[Type, Bool]]):- isCausePresent(Type,Bool).
%searchCause([[Type, Bool]|CrisisList], [[Type, Bool]|Rest]):- isCausePresent(Type, Bool), searchCause(CrisisList, Rest).
%searchCause([PossibleCrisis|CrisisList], Rest):- searchCause(CrisisList, Rest).

%isCausePresent(ATRIB, 1):- hadRecent(ATRIB).
%isCausePresent(ATRIB, 0):- not(hadRecent(ATRIB)).

%getMessageID(MyId,[[Cause|_]], NewID):- atom_concat('_', Cause, Cause_), atom_concat(MyId, Cause_, NewID).
%getMessageID(MyId,[[Cause|_]| CauseList], NewID2):- atom_concat('_', Cause, Cause_), atom_concat(MyId, Cause_, NewID), getMessageID(NewID, CauseList, NewID2).


%%------------------------------------------------------------------------------------------------------------
%% Auxiliary function to print the list of advices
%%------------------------------------------------------------------------------------------------------------

printLista([]).
printLista([HEAD|REST]):- write(HEAD), nl, printLista(REST).

element_at(X,[X|_],1).
element_at(X,[_|L],K) :- element_at(X,L,K1), K is K1 + 1.


%%---------------------------------------------------

quick_sortado(List,Sorted):-q_sortid(List,[],Sorted).
q_sortid([],Acc,Acc).
q_sortid([H|T],Acc,Sorted):-
    pivotinga(H,T,L1,L2),
    q_sortid(L1,Acc,Sorted1), q_sortid(L2,[H|Sorted1], Sorted).


pivotinga([_|_],[],[],[]).
pivotinga([ID|Urg],[[IDX|X]|T],[[IDX|X]|L],G):- X =< Urg, pivotinga([ID|Urg],T,L,G).
pivotinga([ID|Urg],[[IDX|X]|T],L,[[IDX|X]|G]):- X > Urg, pivotinga([ID|Urg],T,L,G).

%%---------------------------------------------------

verifyOutDatedParameters(Parameter, [MissingID]) :- not(hasRecently(Parameter)), atom_concat('missing:', Parameter, MissingID).
verifyOutDatedParameters([Parameter|Rest], MissingIDList) :- not(hasRecently(Parameter)), atom_concat('missing:', Parameter, MissingID), verifyOutDatedParameters(Rest, MissingIDList).


minNumAdv(Num1,Num2,Num1):- Num1<Num2.
minNumAdv(Num1,Num2,Num2).

replace_fact(OldFact, NewFact) :-
    (   call(OldFact)
    ->  retract(OldFact),
        assertz(NewFact)
    ;   true
    ).

