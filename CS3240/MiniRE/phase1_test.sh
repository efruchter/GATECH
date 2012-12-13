for dir in phase1_test_inputs/*; do
    echo $dir
    echo -n "ACTUAL: "
    ./run_phase1.sh $dir/spec $dir/input | shasum
    echo -n "EXPECTED: "
    cat $dir/output | shasum
    echo
done
