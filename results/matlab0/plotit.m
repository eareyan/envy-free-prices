y = net(x,xi,ai);
yout = cell2mat(y);
rev = mapminmax('reverse',yout,PS_out);
orig = M(choice,:);
e = gsubtract(t,y);
performance = perform(net,t,y)
line = linspace(1,sizeM(2),sizeM(2));
plot(line(1:end-length(inputDelays)+1),orig(1:end-length(inputDelays)+1),line(1:end-length(inputDelays)+1),rev,line(1:end-length(inputDelays)+1),zeros(sizeM(2)-length(inputDelays)+1)) 