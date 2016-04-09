
%alldata = csvread('/home/eareyanv/workspace/envy-free-prices/results/results-unit-demand.csv',1);
%mean(alldata(:,[4:4]) ./ alldata(:,[6:6]))
%std(alldata(:,[4:4]) ./ alldata(:,[6:6]))


%mean(alldata(:,[4:4]) ./ alldata(:,[8:8]))
%std(alldata(:,[4:4]) ./ alldata(:,[8:8]))

%mean(alldata(:,[8:8]) ./ alldata(:,[6:6]))

alldata = csvread('/home/eareyanv/workspace/envy-free-prices/results/results-unit-comparison.csv',1);
mean(alldata(:,[6:6]) ./ alldata(:,[4:4]))