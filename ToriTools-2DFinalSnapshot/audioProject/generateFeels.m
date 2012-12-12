function generateFeels (songName, detectLength, beatFactor, smoothLevel, beatSpacing)

    [soundMatrix, sampleRate] = wavread([songName, '.wav']);
    
    soundMatrix = soundMatrix(1:end, 1);
    
    %time = (1:length(soundMatrix))';
    
    %subplot(2,2,1);
    
    %plot(time, soundMatrix);
    %title('Signal Amplitude');
    %xlabel('Time');
    %ylabel('Amplitude');    
    
    samplesPerMillisecond = sampleRate / 1000;
    
    %Condense the signal down to 1 millisecond intervals
    
    averages = 1:floor(length(soundMatrix)/ samplesPerMillisecond);
    
    index = 0;
    for i = 1 : floor(length(soundMatrix) / samplesPerMillisecond)
        index = index + 1;
        energy = 0;
        for a = 1 : samplesPerMillisecond
            energy = energy + abs(soundMatrix(i * floor(samplesPerMillisecond) + a));
        end
        averages(floor(index)) = energy / samplesPerMillisecond;
    end
    
    disp('Length in Milliseconds: ');
    disp(length(averages));
    
    disp('Length in min: ');
    disp(length(averages) / 1000 / 60);
    
    %find the beats based on history of length detectLength Milliseconds.
    beats = (1 : length(averages)) .* 0;    
    beatWait = 0;
    for i = detectLength + 1 : length(averages);
        beatWait = beatWait - 1;
        if beatWait < 0
            if averages(i) / mean((averages(i - detectLength : i))) > beatFactor
                beats(i)  = 1;
                beatWait = beatSpacing + 1;
            end
        end
    end    
    %{
        make a skewed vector of feel based on beat values
        skewedFeels = 1 : length(beats);
        for i = 1 : length(beats);
            skewedFeels(i) = (beats(i) * 2 + averages(i)) / 2;
        end    
        skewedFeels = smooth((skewedFeels)', length(skewedFeels) / smoothLevel)';
    %}
    skewedFeels = smooth((averages)', length(averages) / smoothLevel)';
    skewedFeels = smooth((skewedFeels)', 1000)';
    
    to1Scalar = 1 / max(skewedFeels);
    skewedFeels = skewedFeels .* to1Scalar;
    
    clf;
    
    subplot(2,1,1)
    plot(1:length(averages), averages);
    title('Avg. Signal Amplitude per ms');
    xlabel('Time (ms)');
    ylabel('Amplitude (feel)');
    
    hold all;
    
    % plot what will be the feel levels
    plot(1:length(skewedFeels), skewedFeels);
    
    % plot beats
    subplot(2,1,2)
    plot(1:length(beats), beats);
    title('Action Moments');
    xlabel('Time (ms)');
    ylabel('Action (0/1)');
    
    %print to file
    
    fid = fopen([songName, '_beats.kres'],'wt');
    fprintf(fid,'%f\n', beats);
    fclose(fid);
    
    fid = fopen([songName, '_feels.kres'],'wt');
    fprintf(fid,'%f\n', skewedFeels);
    fclose(fid);
       
end