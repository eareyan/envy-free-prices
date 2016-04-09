alldata = csvread('/Users/Daniel/Downloads/results-general-demand-fixed.csv');

alldata(:,17) = alldata(:,10) ./ alldata(:,5); %wf revenue/eff revenue
alldata(:,18) = alldata(:,10) ./ alldata(:,15); %wf revenue/weq revenue
alldata(:,19) = alldata(:,15) ./ alldata(:,5); %weq revenue/eff revenue
alldata(:,20) = alldata(:,7)./ alldata(:,1); %eff num violations/n
alldata(:,21) = alldata(:,8)./ alldata(:,1); %eff value violations/n
alldata(:,22) = alldata(:,9)./ alldata(:,1); %eff ef violations/n
alldata(:,23) = alldata(:,12)./ alldata(:,1); %wf num violations/n
alldata(:,24) = alldata(:,13)./ alldata(:,1); %wf value violations/n
alldata(:,25) = alldata(:,14)./ alldata(:,1); %wf ef violations/n
 
per25 = alldata(alldata(:,3) == .25,:);
per50 = alldata(alldata(:,3) == .50,:);
per75 = alldata(alldata(:,3) == .75,:);
per00 = alldata(alldata(:,3) == 1.00,:);

x = per50(:,1);
y = per50(:,2);
z1 = per50(:,4);%%ratioefficiency
z2 = per50(:,17);%%wf revenue/eff revenue
z3 = per50(:,18);%%wf revenue/weq revenue
z4 = per50(:,19);%%weq revenue/eff revenue
z5 = per50(:,20);%%eff num violations/n
z6 = per50(:,21);%%eff value violations/n
z7 = per50(:,22);%%eff ef violations/n
z8 = per50(:,23);%%wf num violations/n
z9 = per50(:,24);%%wf value violations/n
z10 = per50(:,25);%%wf ef violations/n


graphit(x,y,z1,2,20,1,'ratioefficiency');
graphit(x,y,z2,2,20,2,'wf revenue/eff revenue');
graphit(x,y,z3,2,20,3,'wf revenue/weq revenue');
graphit(x,y,z4,2,20,4,'weq revenue/eff revenue');
graphit(x,y,z5,2,20,5,'eff num violations/n');
graphit(x,y,z6,2,20,6,'eff value violations/n');
graphit(x,y,z7,2,20,7,'eff ef violations/n');
graphit(x,y,z8,2,20,8,'wf num violations/n');
graphit(x,y,z9,2,20,9,'wf value violations/n');
graphit(x,y,z10,2,20,10,'wf ef violations/n');

graphit(x,y,z1,15,20,11,'ratioefficiency');
graphit(x,y,z2,15,20,12,'wf revenue/eff revenue');
graphit(x,y,z3,15,20,13,'wf revenue/weq revenue');
graphit(x,y,z4,15,20,14,'weq revenue/eff revenue');
graphit(x,y,z5,15,20,15,'eff num violations/n');
graphit(x,y,z6,15,20,16,'eff value violations/n');
graphit(x,y,z7,15,20,17,'eff ef violations/n');
graphit(x,y,z8,15,20,18,'wf num violations/n');
graphit(x,y,z9,15,20,19,'wf value violations/n');
graphit(x,y,z10,15,20,20,'wf ef violations/n');



%%PLOTTING OVER%%
%%CURVE FITTING%%
data_select = per00;
num_supply = data_select(:,1).*data_select(:,1);
num_demand = data_select(:,2).*data_select(:,2);
combined_size = data_select(:,1) .* data_select(:,2);
added_size = data_select(:,1) + data_select(:,2);
efftime = data_select(:,6);
wftime = data_select(:,11);
weqtime = data_select(:,16);

timeselect = efftime;
nametime = 'eff time';
[fit_sup,fit_sup_res] = fit(num_supply,timeselect,'poly2');
[fit_dem,fit_dem_res] = fit(num_demand,timeselect,'poly2');
[fit_com,fit_com_res] = fit(combined_size,timeselect,'poly2');
[fit_add,fit_add_res] = fit(added_size,timeselect,'poly2');
[M,I] = max([fit_sup_res.rsquare fit_dem_res.rsquare fit_com_res.rsquare fit_add_res.rsquare]);
namearray = {'supply' 'demand' 'multiplied' 'added'};
resarray = {fit_sup fit_dem fit_com fit_add};
result = resarray{I};
tit = strcat(nametime,' vs ',namearray{I},' total; rsquare=',num2str(M),'; func=',num2str(result.p1),'*x^2 + ',num2str(result.p2),'*x + ',num2str(result.p3));
xarray = {num_supply,num_demand,combined_size,added_size};
figure(21);
plot(result,xarray{I},timeselect);
title(tit);
eff_const = [result.p1,result.p2,result.p3];

timeselect = wftime;
nametime = 'wf time';
[fit_sup,fit_sup_res] = fit(num_supply,timeselect,'poly2');
[fit_dem,fit_dem_res] = fit(num_demand,timeselect,'poly2');
[fit_com,fit_com_res] = fit(combined_size,timeselect,'poly2');
[fit_add,fit_add_res] = fit(added_size,timeselect,'poly2');
[M,I] = max([fit_sup_res.rsquare fit_dem_res.rsquare fit_com_res.rsquare fit_add_res.rsquare]);
namearray = {'supply' 'demand' 'multiplied' 'added'};
resarray = {fit_sup fit_dem fit_com fit_add};
result = resarray{I};
tit = strcat(nametime,' vs ',namearray{I},' total; rsquare=',num2str(M),'; func=',num2str(result.p1),'*x^2 + ',num2str(result.p2),'*x + ',num2str(result.p3));
xarray = {num_supply,num_demand,combined_size,added_size};
figure(22);
plot(result,xarray{I},timeselect);
title(tit);
wf_const = [result.p1,result.p2,result.p3];

timeselect = weqtime;
nametime = 'weq time';
[fit_sup,fit_sup_res] = fit(num_supply,timeselect,'poly2');
[fit_dem,fit_dem_res] = fit(num_demand,timeselect,'poly2');
[fit_com,fit_com_res] = fit(combined_size,timeselect,'poly2');
[fit_add,fit_add_res] = fit(added_size,timeselect,'poly2');
[M,I] = max([fit_sup_res.rsquare fit_dem_res.rsquare fit_com_res.rsquare fit_add_res.rsquare]);
namearray = {'supply' 'demand' 'multiplied' 'added'};
resarray = {fit_sup fit_dem fit_com fit_add};
result = resarray{I};
tit = strcat(nametime,' vs ',namearray{I},' total; rsquare=',num2str(M),'; func=',num2str(result.p1),'*x^2 + ',num2str(result.p2),'*x + ',num2str(result.p3));
xarray = {num_supply,num_demand,combined_size,added_size};
figure(23);
plot(result,xarray{I},timeselect);
title(tit);
weq_const = [result.p1,result.p2,result.p3];

syms xv;
eqn1 = wf_const(1)*xv^2 + wf_const(2)*xv + wf_const(3)== eff_const(1)*xv^2 + eff_const(2)*xv + eff_const(3);
sol1 = double(solve(eqn1,xv));
disp(strcat('wf beats eff at x=',num2str(max(sol1(1),sol1(2)))));

eqn2 = wf_const(1)*xv^2 + wf_const(2)*xv + wf_const(3)== weq_const(1)*xv^2 + weq_const(2)*xv + weq_const(3);
sol2 = double(solve(eqn2,xv));
disp(strcat('wf beats weq at x=',num2str(max(sol2(1),sol2(2)))));