figure

numItems = '25';
numBidders = '10';
i = 1;
connectivity = [0.25,0.5,0.75,1.0]
for p = connectivity
    subplot(2,2,i)
    M = csvread(strcat('/home/eareyanv/workspace/graphs/unitdemand-401-1000-',numItems,'-',numBidders,'-',num2str(p),'.csv'),1);
    plot(M(:,1),M(:,2));
    title(strcat(numItems,' items, ',numBidders,' bidders, p=',num2str(p)))    
    i = i +1;
end



