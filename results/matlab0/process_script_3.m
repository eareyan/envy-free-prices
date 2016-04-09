alldata = csvread('/Users/Daniel/Downloads/results-unit-demand.csv');

alldata(:,13) = alldata(:,4) ./ alldata(:,6); %weq revenue/evapp revenue
alldata(:,14) = alldata(:,8) ./ alldata(:,6); %lp revenue/evapp revenue
alldata(:,15) = alldata(:,4) ./ alldata(:,8); %weq revenue/lp revenue
alldata(:,16) = alldata(:,10)./ alldata(:,1); %num violations/n
alldata(:,17) = alldata(:,11)./ alldata(:,1); %value violations/n
 
per25 = alldata(alldata(:,3) == .25,:);
per50 = alldata(alldata(:,3) == .50,:);
per75 = alldata(alldata(:,3) == .75,:);
per00 = alldata(alldata(:,3) == 1.00,:);

x = per50(:,1);
y = per50(:,2);
z1 = per50(:,13);%%weq revenue/evapp revenue
z2 = per50(:,14);%%lp revenue/evapp revenue
z3 = per50(:,15);%%weq revenue/lp revenue
z4 = per50(:,16);%%num violations/n
z5 = per50(:,17);%%value violations/n

z6 = per25(:,14);%%lp revenue/evapp revenue
z7 = per75(:,14);%%lp revenue/evapp revenue
z8 = per00(:,14);%%lp revenue/evapp revenue

graphit(x,y,z1,2,20,1,'weq revenue/evapp revenue');
graphit(x,y,z2,2,20,2,'lp revenue/evapp revenue');
graphit(x,y,z3,2,20,3,'weq revenue/lp revenue');
graphit(x,y,z4,2,20,4,'num violations/n');
graphit(x,y,z5,2,20,5,'value violations/n');

graphit(x,y,z1,15,20,6,'weq revenue/evapp revenue');
graphit(x,y,z2,15,20,7,'lp revenue/evapp revenue');
graphit(x,y,z3,15,20,8,'weq revenue/lp revenue');
graphit(x,y,z4,15,20,9,'num violations/n');
graphit(x,y,z5,15,20,10,'value violations/n');

graphit(x,y,z6,2,20,11,'lp revenue/evapp revenue, r=.25');
graphit(x,y,z2,2,20,12,'lp revenue/evapp revenue, r=.5');
graphit(x,y,z7,2,20,13,'lp revenue/evapp revenue, r=.75');
graphit(x,y,z8,2,20,14,'lp revenue/evapp revenue, r=1.0');

%%PLOTTING OVER%%
%%CURVE FITTING%%
data_select = per50;
num_supply = data_select(:,1).*data_select(:,1);
num_demand = data_select(:,2).*data_select(:,2);
combined_size = data_select(:,1) .* data_select(:,2);
added_size = data_select(:,1) + data_select(:,2);
weqtime = data_select(:,5);
evapptime = data_select(:,7);
lptime = data_select(:,9);

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
figure(15);
plot(result,xarray{I},timeselect);
title(tit);
weq_const = [result.p1,result.p2,result.p3];

timeselect = evapptime;
nametime = 'evapp time';
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
figure(16);
plot(result,xarray{I},timeselect);
title(tit);
evapp_const = [result.p1,result.p2,result.p3];

timeselect = lptime;
nametime = 'lp time';
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
figure(17);
plot(result,xarray{I},timeselect);
title(tit);
lp_const = [result.p1,result.p2,result.p3];

syms xv;
eqn1 = lp_const(1)*xv^2 + lp_const(2)*xv + lp_const(3)== weq_const(1)*xv^2 + weq_const(2)*xv + weq_const(3);
sol1 = double(solve(eqn1,xv));
disp(strcat('lp beats weq at x=',num2str(max(sol1(1),sol1(2)))));

eqn2 = lp_const(1)*xv^2 + lp_const(2)*xv + lp_const(3)== evapp_const(1)*xv^2 + evapp_const(2)*xv + evapp_const(3);
sol2 = double(solve(eqn2,xv));
disp(strcat('lp beats evapp at x=',num2str(max(sol2(1),sol2(2)))));