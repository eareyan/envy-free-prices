alldata = csvread('/Users/Daniel/Downloads/results-unit-comparison-fixed.csv');

alldata(:,8) = alldata(:,4) ./ alldata(:,6); %weq revenue/evapp revenue
alldata(:,9) = alldata(:,5) ./ alldata(:,7); %weq time/lp time
 
per25 = alldata(alldata(:,3) == .25,:);
per50 = alldata(alldata(:,3) == .50,:);
per75 = alldata(alldata(:,3) == .75,:);
per00 = alldata(alldata(:,3) == 1.00,:);

x = per50(:,1);
y = per50(:,2);
z1 = per50(:,7);%%weq revenue/evapp revenue
z2 = per50(:,8);%%weq time/evapp time


graphit(x,y,z1,2,20,1,'weq revenue/evapp revenue');
graphit(x,y,z1,15,20,2,'weq revenue/evapp revenue');
graphit(x,y,z2,2,20,3,'weq time/evapp time');
graphit(x,y,z2,15,20,4,'weq time/evapp time');

%%PLOTTING OVER%%
%%CURVE FITTING%%
data_select = per50;
num_supply = data_select(:,1).*data_select(:,1);
num_demand = data_select(:,2).*data_select(:,2);
combined_size = data_select(:,1) .* data_select(:,2);
added_size = data_select(:,1) + data_select(:,2);
weqtime = data_select(:,5);
evapptime = data_select(:,7);

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
figure(5);
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
figure(6);
plot(result,xarray{I},timeselect);
title(tit);
evapp_const = [result.p1,result.p2,result.p3];
