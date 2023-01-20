package org.artiom.net;

class KernelLayer {
    protected KernelUnit[] kernels;
    protected Map[] outputs;

    protected KernelLayer(KernelLayer prev, int kernelsNum) {
        outputs = new Map[kernelsNum];
        kernels = new NeuronUnit[kernelsNum];

        for (int i = 0; i < neuronsNum; i++) {
            outputs[i] = 0f;
            neurons[i] = new NeuronUnit(prev.outputs);
        }
    }
}
